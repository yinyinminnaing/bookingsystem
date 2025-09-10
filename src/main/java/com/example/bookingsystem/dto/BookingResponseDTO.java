package com.example.bookingsystem.dto;

import com.example.bookingsystem.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Integer id;
    private LocalDate bookingDate;
    private BookingStatus bookingStatus;
    private Integer classId;
    private String className;
    private LocalTime classStartTime;
    private LocalTime classEndTime;
    private Integer userId;
    private String userName;
    private boolean onWaitlist;
    private Integer waitlistPosition;
}