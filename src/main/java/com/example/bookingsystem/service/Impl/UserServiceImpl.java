package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.dto.ChangePasswordDTO;
import com.example.bookingsystem.dto.ResetPasswordDTO;
import com.example.bookingsystem.dto.ResponseDto;
import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.repository.UserRepository;
import com.example.bookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getProfile(Integer userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("User not found or inactive"));

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public ResponseDto changePassword(Integer userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        return ResponseDto.builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }

    @Override
    public ResponseDto requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseDto.builder()
                    .success(true)
                    .message("If the email exists, a reset link has been sent")
                    .build();
        }

        return ResponseDto.builder()
                .success(true)
                .message("Password reset instructions sent to your email")
                .build();
    }

    @Override
    @Transactional
    public ResponseDto resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOptional = userRepository.findByEmail(resetPasswordDTO.getEmail());

        userOptional.orElseThrow(()-> new RuntimeException("Invalid reset request"));

        User user = userOptional.get();

        // Check if new password matches confirmation
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        return ResponseDto.builder()
                .success(true)
                .message("Password reset successfully")
                .build();
    }

    @Override
    @Transactional
    public ResponseDto deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);

        return ResponseDto.builder()
                .success(true)
                .message("User deactivated successfully")
                .build();
    }
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .isVerified(user.isVerified())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
