package com.example.SmartSpent.application.exception;

/**
 * 登入驗證失敗（帳號不存在 / 密碼錯誤）
 *
 * ⚠️ 注意：
 * - 這是「業務例外」，不是系統錯誤
 * - 用於登入流程中斷
 * - 可攜帶「剩餘嘗試次數」供前端顯示
 */
public class AuthenticationFailedException extends RuntimeException {

    /** 剩餘嘗試次數（可為 null） */
    private final Integer remainingAttempts;

    public AuthenticationFailedException(String message) {
        super(message);
        this.remainingAttempts = null;
    }

    public AuthenticationFailedException(
            String message,
            Integer remainingAttempts
    ) {
        super(message);
        this.remainingAttempts = remainingAttempts;
    }

    public Integer getRemainingAttempts() {
        return remainingAttempts;
    }
}
