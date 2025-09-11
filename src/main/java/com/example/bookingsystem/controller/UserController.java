package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.ChangePasswordDTO;
import com.example.bookingsystem.dto.ResetPasswordDTO;
import com.example.bookingsystem.dto.ResponseDto;
import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile and account management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile", description = "Retrieve user profile information")
    @Parameter(name = "userId", description = "ID of the user", required = true)
    public ResponseEntity<ResponseDto> getProfile(@PathVariable Integer userId) {
        UserDTO userDTO = userService.getProfile(userId);
        return new ResponseEntity<>(ResponseDto.builder()
                .success(true)
                .data(userDTO)
                .build(), HttpStatus.OK);
    }

    @PostMapping("/{userId}/change-password")
    @Operation(summary = "Change password", description = "Change user's password")
    @Parameter(name = "userId", description = "ID of the user", required = true)
    public ResponseEntity<ResponseDto> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        ResponseDto response = userService.changePassword(userId, changePasswordDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/request-reset")
    @Operation(summary = "Request password reset", description = "Request password reset for the given email")
    @Parameter(name = "email", description = "User's email address", required = true)
    public ResponseEntity<ResponseDto> requestPasswordReset(@RequestParam String email) {
        ResponseDto response = userService.requestPasswordReset(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user's password using reset token")
    public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        ResponseDto response = userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account")
    @Parameter(name = "userId", description = "ID of the user to deactivate", required = true)
    public ResponseEntity<ResponseDto> deactivateUser(@PathVariable Integer userId) {
        ResponseDto response = userService.deactivateUser(userId);
        return ResponseEntity.ok(response);
    }
}
