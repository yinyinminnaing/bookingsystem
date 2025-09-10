package com.example.bookingsystem.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private Long expiresIn; // in milliseconds

    public AuthResponse(String token,Long expiresIn, String message) {
        this.token = token;
        this.expiresIn=expiresIn;
        this.message = message;
    }
}
