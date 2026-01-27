package com.example.SmartSpent.infrastructure.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.SmartSpent.domain.value.UserId;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequireLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        UserId userId = (UserId) request.getAttribute("loginUserId");
        if (userId == null) {
            response.sendRedirect("/happyshop/home");
            return false; // 中斷，不進 Controller
        }
        return true; // 放行
    }
}
