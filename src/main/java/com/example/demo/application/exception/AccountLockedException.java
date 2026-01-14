package com.example.demo.application.exception;

/**
 * 帳號已鎖定（業務例外）
 *
 * 用於登入流程中：
 * - 錯誤次數達上限
 * - 或帳號已處於鎖定狀態
 */
public class AccountLockedException extends RuntimeException {

    public AccountLockedException(String message) {
        super(message);
    }
}
