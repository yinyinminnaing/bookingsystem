package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.*;

public interface AuthService {
    UserDTO registerUser(RegisterRequest registerDTO);
    AuthResponse authenticate(LoginRequest request);
    UserDTO getProfile(Integer userId);
    ResponseDto changePassword(Integer userId, ChangePasswordDTO changePasswordDTO);
    ResponseDto requestPasswordReset(String email);
    ResponseDto resetPassword(ResetPasswordDTO resetPasswordDTO);
    ResponseDto deactivateUser(Integer userId);
}
