package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistResponseDTO {
    private Long id;
    private Integer classId;
    private String className;
    private LocalDateTime joinTime;
    private String status;
    private Integer position;
    private Integer userId;
    private String userName;
}