// UserController.java
package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.*;
import com.example.bookingsystem.service.AuthService;
import com.example.bookingsystem.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse= authService.authenticate(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<ResponseDto> getProfile(@PathVariable Integer userId) {
        UserDTO userDTO = authService.getProfile(userId);
            return new ResponseEntity<>(ResponseDto.builder()
                    .success(true)
                    .data(userDTO)
                    .build(),HttpStatus.OK);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ResponseDto> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
            ResponseDto response = authService.changePassword(userId, changePasswordDTO);
            return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/request-reset")
    public ResponseEntity<ResponseDto> requestPasswordReset(@RequestParam String email) {
            ResponseDto response = authService.requestPasswordReset(email);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
            ResponseDto response = authService.resetPassword(resetPasswordDTO);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<ResponseDto> deactivateUser(@PathVariable Integer userId) {
            ResponseDto response = authService.deactivateUser(userId);
            return ResponseEntity.ok(response);
    }
}