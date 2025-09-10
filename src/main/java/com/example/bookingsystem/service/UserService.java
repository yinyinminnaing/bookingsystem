package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.ChangePasswordDTO;
import com.example.bookingsystem.dto.ResetPasswordDTO;
import com.example.bookingsystem.dto.ResponseDto;
import com.example.bookingsystem.dto.UserDTO;

public interface UserService {
    UserDTO getProfile(Integer userId);
    ResponseDto changePassword(Integer userId, ChangePasswordDTO changePasswordDTO);
    ResponseDto requestPasswordReset(String email);
    ResponseDto resetPassword(ResetPasswordDTO resetPasswordDTO);
    ResponseDto deactivateUser(Integer userId);
}
