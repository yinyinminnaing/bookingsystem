package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.BookingRequestDTO;
import com.example.bookingsystem.dto.BookingResponseDTO;
import com.example.bookingsystem.dto.ClassResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.service.BookingService;
import com.example.bookingsystem.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/book")
    public ResponseEntity<BookingResponseDTO> bookClass(@Valid @RequestBody BookingRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        BookingResponseDTO booking = bookingService.bookClass(user.getId(), request.getClassId(), request.getUserPurchaseId());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        BookingResponseDTO cancelledBooking = bookingService.cancelBooking(user.getId(), bookingId);
        return ResponseEntity.ok(cancelledBooking);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<BookingResponseDTO> bookings = bookingService.getUserBookings(user.getId());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-confirmed-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyConfirmedBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<BookingResponseDTO> bookings = bookingService.getUserConfirmedBookings(user.getId());
        return ResponseEntity.ok(bookings);
    }
}