package com.example.demo.application.exception;

public class PasswordInvalidException extends IllegalArgumentException {

    public PasswordInvalidException(String message) {
        super(message);
    }
}

