package com.example.mortgage_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotMatchMaturityPeriodException.class)
    public ResponseEntity<String> handleNotMatchMaturityPeriodException(NotMatchMaturityPeriodException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}