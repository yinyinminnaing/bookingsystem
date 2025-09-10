package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO bookClass(Integer userId, Integer classId, Integer userPurchaseId);
    BookingResponseDTO cancelBooking(Integer userId, Integer bookingId);
    List<BookingResponseDTO> getUserBookings(Integer userId);
    List<BookingResponseDTO> getUserConfirmedBookings(Integer userId);
}
