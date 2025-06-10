package com.example.mortgage_service.exception;

public class NotMatchMaturityPeriodException extends RuntimeException {
    public NotMatchMaturityPeriodException(String message) {
        super(message);
    }

    public NotMatchMaturityPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}
