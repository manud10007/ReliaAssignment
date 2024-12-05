package com.reliaquest.api.exceptions;

public class EmployeeApiException extends RuntimeException {
    public EmployeeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
