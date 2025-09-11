package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Booking;
import com.example.bookingsystem.entity.BookingStatus;
import com.example.bookingsystem.entity.Classes;
import com.example.bookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);

    List<Booking> findByUserAndBookingStatus(User user, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.bookingStatus = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByUser(@Param("user") User user);

    long countByClassesAndBookingStatus(Classes classes, BookingStatus status);

    Optional<Booking> findByUserAndClassesAndBookingStatus(User user, Classes classes, BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.classes = :classes AND b.bookingStatus = 'CONFIRMED'")
    int countConfirmedBookingsForClass(@Param("classes") Classes classes);

    List<Booking> findByUserAndBookingDate(User user, LocalDate bookingDate);
}