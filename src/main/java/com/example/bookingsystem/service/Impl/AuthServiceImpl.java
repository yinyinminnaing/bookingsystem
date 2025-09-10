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
    public AuthResponse authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User authenticated=userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow();
        String jwtToken = jwtUtil.generateToken(authenticated);
        return new AuthResponse(jwtToken, jwtUtil.getJwtExpiration(),"Login successful");
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
