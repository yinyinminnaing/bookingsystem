package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.dto.BookingResponseDTO;
import com.example.bookingsystem.entity.*;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.ClassesRepository;
import com.example.bookingsystem.repository.UserPurchasesRepository;
import com.example.bookingsystem.repository.WaitlistRepository;
import com.example.bookingsystem.service.BookingService;
import com.example.bookingsystem.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClassesRepository classesRepository;
    private final UserPurchasesRepository userPurchasesRepository;
    private final WaitlistRepository waitlistRepository;
    private final CreditService creditService;

    @Override
    @Transactional
    public BookingResponseDTO bookClass(Integer userId, Integer classId, Integer userPurchaseId) {
        User user = getUserById(userId);
        Classes classes = getClassById(classId);
        UserPurchases userPurchase = getUserPurchase(userPurchaseId, userId);

        // Check if user already has a booking for this class
        if (bookingRepository.findByUserAndClassesAndBookingStatus(user, classes, BookingStatus.CONFIRMED).isPresent()) {
            throw new RuntimeException("User already has a booking for this class");
        }

        // Check if user is on waitlist for this class
        Optional<WaitingLists> existingWaitlist = waitlistRepository.findByUserAndClasses(user, classes);
        if (existingWaitlist.isPresent()) {
            throw new RuntimeException("User is already on waitlist for this class");
        }

        int currentBookings = bookingRepository.countConfirmedBookingsForClass(classes);
        boolean classFull = currentBookings >= classes.getMaxCapacity();

        if (classFull) {
            // Add to waitlist and deduct credits
            return addToWaitlist(user, classes, userPurchase);
        } else {
            // Create direct booking
            return createBooking(user, classes, userPurchase);
        }
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

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Refund credits
        creditService.refundCredits(booking.getUserPackage(), booking.getClasses().getRequiredCredits());

        // Check waitlist and promote next user
        promoteFromWaitlist(booking.getClasses());

        return convertToDTO(booking);
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

    private BookingResponseDTO createBooking(User user, Classes classes, UserPurchases userPurchase) {
        // Deduct credits first
        creditService.deductCredits(userPurchase, classes.getRequiredCredits());

        Booking booking = Booking.builder()
                .user(user)
                .classes(classes)
                .userPackage(userPurchase)
                .bookingDate(LocalDate.now())
                .bookingStatus(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    private BookingResponseDTO addToWaitlist(User user, Classes classes, UserPurchases userPurchase) {
        // Deduct credits for waitlist
        creditService.deductCredits(userPurchase, classes.getRequiredCredits());

        WaitingLists waitlist = WaitingLists.builder()
                .user(user)
                .classes(classes)
                .userPurchases(userPurchase)
                .joinedDate(LocalDateTime.now())
                .action(WaitingAction.WAITING)
                .deductedCredits(classes.getRequiredCredits())
                .build();

        WaitingLists savedWaitlist = waitlistRepository.save(waitlist);

        BookingResponseDTO response = convertToDTO(null);
        response.setOnWaitlist(true);
        response.setWaitlistPosition(getWaitlistPosition(savedWaitlist));
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
        // Implementation depends on your UserRepository
        return null; // Replace with actual implementation
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
}