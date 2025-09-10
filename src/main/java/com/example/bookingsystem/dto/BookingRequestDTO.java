package com.example.bookingsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {
    @NotNull(message = "Class ID is required")
    private Integer classId;

    @NotNull(message = "User purchase ID is required")
    private Integer userPurchaseId;
}