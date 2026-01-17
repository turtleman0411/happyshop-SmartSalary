package com.example.demo.presentation.dto.response;

import com.example.demo.domain.value.UserId;

public record LoginResult(
    boolean success,
    UserId userId,
    String errorMessage
) {
    public static LoginResult success(UserId userId) {
        return new LoginResult(true, userId, null);
    }

    public static LoginResult fail(String msg) {
        return new LoginResult(false, null, msg);
    }
}
