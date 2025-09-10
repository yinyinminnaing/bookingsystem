package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.ChangePasswordDTO;
import com.example.bookingsystem.dto.ResetPasswordDTO;
import com.example.bookingsystem.dto.ResponseDto;
import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ResponseDto> getProfile(@PathVariable Integer userId) {
        UserDTO userDTO = userService.getProfile(userId);
        return new ResponseEntity<>(ResponseDto.builder()
                .success(true)
                .data(userDTO)
                .build(), HttpStatus.OK);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ResponseDto> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        ResponseDto response = userService.changePassword(userId, changePasswordDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/request-reset")
    public ResponseEntity<ResponseDto> requestPasswordReset(@RequestParam String email) {
        ResponseDto response = userService.requestPasswordReset(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        ResponseDto response = userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<ResponseDto> deactivateUser(@PathVariable Integer userId) {
        ResponseDto response = userService.deactivateUser(userId);
        return ResponseEntity.ok(response);
    }
}
