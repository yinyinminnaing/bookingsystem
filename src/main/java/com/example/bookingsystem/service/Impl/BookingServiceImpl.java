package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.dto.BookingRequestDTO;
import com.example.bookingsystem.dto.BookingResponseDTO;
import com.example.bookingsystem.entity.*;
import com.example.bookingsystem.repository.*;
import com.example.bookingsystem.service.BookingService;
import com.example.bookingsystem.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClassesRepository classesRepository;
    private final UserPurchasesRepository userPurchasesRepository;
    private final WaitlistRepository waitlistRepository;
    private final CreditService creditService;
    private final UserRepository userRepository;
    private final RedisLockService redisLockService;
    private final RedisWaitlistService redisWaitlistService;


    @Transactional
    public BookingResponseDTO bookClass(Integer userId, Integer classId, Integer userPurchaseId) {
        String lockKey = "booking:class:" + classId;

        if (!redisLockService.acquireLock(lockKey)) {
            throw new RuntimeException("Could not acquire lock for booking. Please try again.");
        }

        try {
            User user = getUserById(userId);
            Classes classes = getClassById(classId);
            UserPurchases userPurchase = getUserPurchase(userPurchaseId, userId);

            validateBookingPreconditions(user, classes, userPurchase);

            int currentBookings = bookingRepository.countConfirmedBookingsForClass(classes);
            boolean classFull = currentBookings >= classes.getMaxCapacity();

            if (classFull) {
                return addToWaitlist(user, classes, userPurchase);
            } else {
                Booking booking = createBooking(user, classes, userPurchase);
                return convertToDTO(booking);
            }
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }

    private void validateBookingPreconditions(User user, Classes classes, UserPurchases userPurchase) {
        // Check time overlap
        if (hasTimeOverlap(user, classes)) {
            throw new RuntimeException("User has overlapping class booking");
        }

        // Check if user already has a booking for this class
        if (bookingRepository.findByUserAndClassesAndBookingStatus(user, classes, BookingStatus.CONFIRMED).isPresent()) {
            throw new RuntimeException("User already has a booking for this class");
        }

        // Check credit balance
        if (userPurchase.getRemainingCredits() < classes.getRequiredCredits()) {
            throw new RuntimeException("Insufficient credits");
        }
    }

    private boolean hasTimeOverlap(User user, Classes newClass) {
        LocalDate today = LocalDate.now();
        List<Booking> userBookings = bookingRepository.findByUserAndBookingDate(user, today);

        for (Booking existingBooking : userBookings) {
            if (isTimeOverlap(existingBooking.getClasses(), newClass)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeOverlap(Classes class1, Classes class2) {
        LocalTime start1 = class1.getStartTime();
        LocalTime end1 = class1.getEndTime();
        LocalTime start2 = class2.getStartTime();
        LocalTime end2 = class2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Check if cancellation is allowed (4 hours before class)
        LocalDateTime classStartDateTime = LocalDateTime.of(booking.getBookingDate(), booking.getClasses().getStartTime());
        LocalDateTime cancellationDeadline = classStartDateTime.minusHours(4);

        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new RuntimeException("Cancellation not allowed within 4 hours of class start time");
        }

        String lockKey = "booking:class:" + booking.getClasses().getId();
        if (!redisLockService.acquireLock(lockKey)) {
            throw new RuntimeException("Could not process cancellation. Please try again.");
        }

        try {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            Booking savedBooking = bookingRepository.save(booking);

            // Refund credits
            creditService.refundCredits(booking.getUserPackage(), booking.getClasses().getRequiredCredits());

            // Process waitlist
            processWaitlist(booking.getClasses());

            return convertToDTO(savedBooking);
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }



    @Override
    public List<BookingResponseDTO> getUserBookings(Integer userId) {
        User user = getUserById(userId);
        return bookingRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getUserConfirmedBookings(Integer userId) {
        User user = getUserById(userId);
        return bookingRepository.findConfirmedBookingsByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Booking createBooking(User user, Classes classes, UserPurchases userPurchase) {
        // Deduct credits first
        creditService.deductCredits(userPurchase, classes.getRequiredCredits());

        Booking booking = Booking.builder()
                .user(user)
                .classes(classes)
                .userPackage(userPurchase)
                .bookingDate(LocalDate.now())
                .bookingStatus(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    private BookingResponseDTO addToWaitlist(User user, Classes classes, UserPurchases userPurchase) {
        // Deduct credits for waitlist
        creditService.deductCredits(userPurchase, classes.getRequiredCredits());

        // Add to Redis waitlist
        redisWaitlistService.addToWaitlist(classes.getId(), user.getId());

        WaitingLists waitlist = WaitingLists.builder()
                .user(user)
                .classes(classes)
                .userPurchases(userPurchase)
                .joinedDate(LocalDateTime.now())
                .action(WaitingAction.WAITING)
                .deductedCredits(classes.getRequiredCredits())
                .build();

        waitlistRepository.save(waitlist);

        Long position = redisWaitlistService.getWaitlistPosition(classes.getId(), user.getId());

        BookingResponseDTO response = convertToDTO(null);
        response.setOnWaitlist(true);
        response.setWaitlistPosition(position != null ? position.intValue() : null);
        return response;
    }

    private void promoteFromWaitlist(Classes classes) {
        List<WaitingLists> pendingWaitlists = waitlistRepository.findPendingWaitlistsByClassOrderByJoinTime(classes);

        if (!pendingWaitlists.isEmpty()) {
            WaitingLists nextWaitlist = pendingWaitlists.get(0);

            // Create booking for waitlisted user
            createBooking(nextWaitlist.getUser(), classes, nextWaitlist.getUserPurchases());

            // Remove from waitlist
            nextWaitlist.setAction(WaitingAction.JOINED);
            waitlistRepository.save(nextWaitlist);
        }
    }
    private void processWaitlist(Classes classes) {
        Integer nextUserId = redisWaitlistService.getNextWaitlistUser(classes.getId());
        if (nextUserId != null) {
            log.info("Processing waitlist for class {}: user {}", classes.getId(), nextUserId);
            // Remove from database waitlist
            Optional<WaitingLists> waitlistEntry = waitlistRepository.findByUserAndClasses(
                    getUserById(nextUserId), classes
            );

            waitlistEntry.ifPresent(entry -> {
                entry.setAction(WaitingAction.JOINED);
                waitlistRepository.save(entry);
            });
        }
    }

    private int getWaitlistPosition(WaitingLists waitlist) {
        List<WaitingLists> pendingWaitlists = waitlistRepository.findPendingWaitlistsByClassOrderByJoinTime(waitlist.getClasses());
        for (int i = 0; i < pendingWaitlists.size(); i++) {
            if (pendingWaitlists.get(i).getId().equals(waitlist.getId())) {
                return i + 1;
            }
        }
        return -1;
    }


    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow();

    }

    private Classes getClassById(Integer classId) {
        return classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    private UserPurchases getUserPurchase(Integer userPurchaseId, Integer userId) {
        return userPurchasesRepository.findByIdAndUser(userPurchaseId, getUserById(userId))
                .orElseThrow(() -> new RuntimeException("User purchase not found or not owned by user"));
    }

    private BookingResponseDTO convertToDTO(Booking booking) {
        if (booking == null) {
            return BookingResponseDTO.builder().build();
        }

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .bookingStatus(booking.getBookingStatus())
                .classId(booking.getClasses().getId())
                .className(booking.getClasses().getClassName())
                .classStartTime(booking.getClasses().getStartTime())
                .classEndTime(booking.getClasses().getEndTime())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getUsername())
                .onWaitlist(false)
                .build();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void processWaitlists() {
        log.info("Processing all waitlists...");
    }

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void refundWaitlistedUsers() {
        log.info("Processing refunds for waitlisted users of ended classes...");
        LocalDateTime now = LocalDateTime.now();

        // Find classes that ended and have waitlisted users
        List<Classes> endedClasses = classesRepository.findEndedClassesWithWaitlist(now);

        for (Classes classes : endedClasses) {
            processWaitlistRefunds(classes);
        }
    }

    private void processWaitlistRefunds(Classes classes) {
        List<Integer> waitlistedUserIds = redisWaitlistService.getWaitlist(classes.getId());
        for (Integer userId : waitlistedUserIds) {
            Optional<WaitingLists> waitlistEntry = waitlistRepository.findByUserAndClasses(
                    getUserById(userId), classes
            );

            waitlistEntry.ifPresent(entry -> {
                if (entry.getAction() == WaitingAction.WAITING) {
                    // Refund credits
                    creditService.refundCredits(entry.getUserPurchases(), entry.getDeductedCredits());
                    entry.setAction(WaitingAction.REFUNDED);
                    waitlistRepository.save(entry);
                }
            });
        }

        // Clear Redis waitlist
        redisWaitlistService.getWaitlist(classes.getId()).clear();
    }
}