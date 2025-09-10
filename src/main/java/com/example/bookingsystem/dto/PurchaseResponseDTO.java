package com.example.bookingsystem.dto;

import com.example.bookingsystem.entity.PurchaseStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDTO {
    private Integer id;
    private LocalDate purchasedDate;
    private LocalDate expiredDate;
    private PurchaseStatus purchaseStatus;
    private Integer packageId;
    private String packageName;
    private Integer credits;
}
