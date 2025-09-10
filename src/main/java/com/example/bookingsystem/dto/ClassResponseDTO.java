package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDTO {
    private Integer id;
    private String className;
    private LocalTime startTime;
    private LocalTime endTime;
    private int courseDuration;
    private int requiredCredits;
    private int maxCapacity;
    private int currentBookings;
    private boolean hasAvailableSlots;
    private Integer countryId;
    private String countryName;
}