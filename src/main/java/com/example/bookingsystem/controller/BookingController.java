package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.BookingRequestDTO;
import com.example.bookingsystem.dto.BookingResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bookings", description = "Class booking management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/book")
    @Operation(summary = "Book a class", description = "Book a class using user's active package credits")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class booked successfully"),
            @ApiResponse(responseCode = "400", description = "Class full, user added to waitlist"),
            @ApiResponse(responseCode = "409", description = "User already has booking for this class")
    })
    public ResponseEntity<BookingResponseDTO> bookClass(@Valid @RequestBody BookingRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        BookingResponseDTO booking = bookingService.bookClass(user.getId(), request.getClassId(), request.getUserPurchaseId());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/cancel/{bookingId}")
    @Operation(summary = "Cancel booking", description = "Cancel a confirmed booking and refund credits")
    @Parameter(name = "bookingId", description = "ID of the booking to cancel", required = true)
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        BookingResponseDTO cancelledBooking = bookingService.cancelBooking(user.getId(), bookingId);
        return ResponseEntity.ok(cancelledBooking);
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get user bookings", description = "Retrieve all bookings for the authenticated user")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<BookingResponseDTO> bookings = bookingService.getUserBookings(user.getId());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-confirmed-bookings")
    @Operation(summary = "Get confirmed bookings", description = "Retrieve only confirmed bookings for the authenticated user")
    public ResponseEntity<List<BookingResponseDTO>> getMyConfirmedBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<BookingResponseDTO> bookings = bookingService.getUserConfirmedBookings(user.getId());
        return ResponseEntity.ok(bookings);
    }
}