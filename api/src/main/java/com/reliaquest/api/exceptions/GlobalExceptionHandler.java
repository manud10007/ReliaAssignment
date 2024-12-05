package com.reliaquest.api.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.reliaquest.api.model.ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        com.reliaquest.api.model.ErrorResponse errorResponse = new com.reliaquest.api.model.ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<com.reliaquest.api.model.ErrorResponse> handleRestClientException(RestClientException ex) {
        log.error("REST Client error occurred", ex);

        com.reliaquest.api.model.ErrorResponse errorResponse = new com.reliaquest.api.model.ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "External service communication error",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.reliaquest.api.model.ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument provided", ex);

        com.reliaquest.api.model.ErrorResponse errorResponse = new com.reliaquest.api.model.ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input provided",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
