package com.example.SmartSpent.application.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException() {
        super("帳號已存在");
    }


}
