package com.example.SmartSpent.presentation.dto.request;

public record LoginForm(
    String username,
    String password,
    boolean rememberMe
) {}
