package com.example.SmartSpent.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.SmartSpent.application.security.RememberMeService;
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

    rememberMeService.authenticate(request)
            .ifPresent(userId -> attachLoginUser(request, userId));

    return true;
}

private void attachLoginUser(HttpServletRequest request, UserId userId) {
    // 給 Controller / Flow 用
    request.setAttribute("loginUserId", userId);

    // ⚠️ 關鍵：補 session（給 redirect / 舊流程用）
    request.getSession().setAttribute("loginUserId", userId);
}

}
