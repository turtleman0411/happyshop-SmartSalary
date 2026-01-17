package com.example.SmartSpent.application.exception;

public class PasswordInvalidException extends IllegalArgumentException {

    public PasswordInvalidException(String message) {
        super(message);
    }
}

