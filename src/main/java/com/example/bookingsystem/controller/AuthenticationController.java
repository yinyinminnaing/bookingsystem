// UserController.java
package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.*;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.service.AuthService;
import com.example.bookingsystem.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody RegisterRequest registerDTO) {
            UserDTO userDTO = authService.registerUser(registerDTO);
            return new ResponseEntity<>(ResponseDto.builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(userDTO)
                    .build(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse= authService.authenticate(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }



}