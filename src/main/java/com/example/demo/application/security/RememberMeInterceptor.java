package com.example.demo.application.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.domain.value.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RememberMeInterceptor implements HandlerInterceptor {

    private final RememberMeService rememberMeService;

    public RememberMeInterceptor(RememberMeService rememberMeService) {
        this.rememberMeService = rememberMeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

    String path = request.getRequestURI();

    // login / register 永遠不跑
    if (path.startsWith("/happyshop/login")
        || path.startsWith("/happyshop/register")) {
        return true;
    }

    // ✅ 關鍵：如果是「剛登出導回來的 home」
    if ("1".equals(request.getHeader("X-LOGOUT"))) {
        return true;
    }
System.out.println("PATH = " + path);


        UserId userId = null;

        // 1️⃣ session（一次性登入 flow）
        var session = request.getSession(false);
        if (session != null) {
            userId = (UserId) session.getAttribute("loginUserId");
        }

        // 2️⃣ remember-me（長期登入）
        if (userId == null) {
            userId = rememberMeService
                    .resolveUser(request)
                    .orElse(null);
        }

        // 3️⃣ 放 request scope
        if (userId != null) {
            request.setAttribute("loginUserId", userId);
        }

        return true;
    }
}
