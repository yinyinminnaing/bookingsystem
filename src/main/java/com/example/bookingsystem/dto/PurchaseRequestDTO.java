package com.example.bookingsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequestDTO {
    @NotNull(message = "Package ID is required")
    private Integer packageId;
}
