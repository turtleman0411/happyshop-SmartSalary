package com.example.demo.presentation.dto.response;

/**
 * 註冊成功回應
 *
 * - 只回傳「流程完成後，前端需要的結果」
 * - 不暴露 Domain Entity
 * - 不暴露 Value Object
 */
public record RegisterResponse(
        String userId
) {}
