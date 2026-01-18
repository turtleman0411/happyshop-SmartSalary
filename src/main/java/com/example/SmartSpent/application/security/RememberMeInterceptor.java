package com.example.SmartSpent.application.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.SmartSpent.domain.value.UserId;

@Component
public class RememberMeInterceptor implements HandlerInterceptor {

    private final RememberMeService rememberMeService;

    public RememberMeInterceptor(RememberMeService rememberMeService) {
        this.rememberMeService = rememberMeService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        // 🔐 嘗試自動登入（是否成功由 Service 決定）
        rememberMeService.authenticate(request)
                .ifPresent(userId ->
                        attachLoginUser(request, userId)
                );

        // 一律放行，流程交給 Controller / Flow
        return true;
    }

    private void attachLoginUser(HttpServletRequest request, UserId userId) {
        request.setAttribute("loginUserId", userId);
    }
}
