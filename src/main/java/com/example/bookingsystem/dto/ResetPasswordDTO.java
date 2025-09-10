package com.example.bookingsystem.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String email;
    private String token;
    private String newPassword;
    private String confirmPassword;
}
