package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageResponseDTO {
    private Integer id;
    private String packageName;
    private int credits;
    private BigDecimal prices;
    private LocalDate expiredDate;
    private String status;
    private boolean isActive;
    private Integer countryId;
    private String countryName;
}
