package com.example.demo.diet.controller;

import com.example.demo.diet.dto.common.ErrorResponse;
import com.example.demo.diet.dto.exception.*;
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
}