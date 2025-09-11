package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.*;
import com.example.bookingsystem.entity.User;

public interface AuthService {
    UserDTO registerUser(RegisterRequest registerDTO);
    AuthResponse authenticate(LoginRequest request);
    void verifyEmail(String email, String token);

}
