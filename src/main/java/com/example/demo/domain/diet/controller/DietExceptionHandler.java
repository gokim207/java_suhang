package com.example.demo.domain.diet.controller;

import com.example.demo.global.common.ErrorResponse;
import com.example.demo.global.exception.DietNotFoundException;
import com.example.demo.global.exception.DietServerException;
import com.example.demo.global.exception.InvalidDietRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DietExceptionHandler {

    @ExceptionHandler(InvalidDietRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDietRequestException(InvalidDietRequestException e) {
        log.warn("Invalid request: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DietNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDietNotFoundException(DietNotFoundException e) {
        log.warn("Diet not found: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DietServerException.class)
    public ResponseEntity<ErrorResponse> handleDietServerException(DietServerException e) {
        log.error("Server error: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // 400
    }
}