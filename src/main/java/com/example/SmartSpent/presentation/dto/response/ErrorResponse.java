package com.example.SmartSpent.presentation.dto.response;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final String code;        // 錯誤代碼（給前端判斷）
    private final String message;     // 人類可讀訊息
    private final int status;         // HTTP 狀態碼
    private final LocalDateTime time; // 發生時間

    public ErrorResponse(
            String code,
            String message,
            int status
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.time = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
