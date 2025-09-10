package com.example.bookingsystem.service.Impl;

import com.example.bookingsystem.dto.*;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.repository.UserRepository;
import com.example.bookingsystem.service.AuthService;
import com.example.bookingsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    //old
    @Override
    @Transactional
    public UserDTO registerUser(RegisterRequest registerDTO) {

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .userName(registerDTO.getUserName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .isVerified(false)
                .isActive(true)
                .createdAt(LocalDate.now())
                .build();

        User savedUser = userRepository.save(user);

       // String verificationToken = UUID.randomUUID().toString();
       // String verificationLink = "http://localhost:8080/api/auth/verify-email?token=" + verificationToken + "&email=" + savedUser.getEmail();
       // boolean emailSent = emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);

        /*if (!emailSent) {
            System.err.println("Failed to send verification email to: " + savedUser.getEmail());
        }*/

        return convertToDTO(savedUser);
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String jwt = jwtUtil.generateToken(authentication.getName());
            return new AuthResponse(jwt,jwtUtil.getJwtExpiration() ,"Login successful");
        } catch (Exception e) {
            return new AuthResponse(null, null,"Invalid credentials");
        }
    }

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
                .userName(user.getUserName())
                .email(user.getEmail())
                .isVerified(user.isVerified())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
