// GlobalExceptionHandler.java
package com.example.bookingsystem.handler;

import com.example.bookingsystem.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseDto.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(ResponseDto.builder()
                        .success(false)
                        .message("An unexpected error occurred")
                        .build());
    }
}