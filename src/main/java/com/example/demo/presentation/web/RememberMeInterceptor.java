package com.example.demo.presentation.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.application.security.RememberMeService;
import com.example.demo.domain.value.UserId;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RememberMeInterceptor implements HandlerInterceptor {

    private final RememberMeService rememberMeService;

    public RememberMeInterceptor(RememberMeService rememberMeService) {
        this.rememberMeService = rememberMeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();

        Object loginUserId = session.getAttribute("loginUserId");
        if (loginUserId != null) return true;

        rememberMeService.resolveUser(request).ifPresent((UserId uid) -> {
            session.setAttribute("loginUserId", uid);
        });

        return true;
    }
}
