package com.example.SmartSpent.application.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.SmartSpent.domain.value.UserId;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestDebugFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestDebugFilter.class);

    private final RememberMeService rememberMeService;

    public RequestDebugFilter(RememberMeService rememberMeService) {
        this.rememberMeService = rememberMeService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String rid = UUID.randomUUID().toString().substring(0, 8);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String full = (qs == null ? uri : uri + "?" + qs);

        String xfProto = request.getHeader("X-Forwarded-Proto");
        String xfFor = request.getHeader("X-Forwarded-For");

        // request attribute（Interceptor attach 的）
        Object attrUser = request.getAttribute("loginUserId");

        // session（如果你某些地方還在用）
        HttpSession session = request.getSession(false);
        Object sessionUser = (session == null ? null : session.getAttribute("loginUserId"));

        // cookie 基本狀態（不印敏感值）
        Cookie[] cookies = request.getCookies();
        boolean hasRemember = hasCookie(cookies, RememberMeService.COOKIE_NAME);
        Integer rememberLen = cookieLen(cookies, RememberMeService.COOKIE_NAME);

        // 嘗試驗證（只顯示「成功/失敗」與 userId）
        Optional<UserId> authByCookie = Optional.empty();
        try {
            authByCookie = rememberMeService.authenticate(request);
        } catch (Exception e) {
            // authenticate 例外也要印出來（你現在追 bug 用）
            log.error("[RID:{}] authenticate() EX: {}", rid, e.toString(), e);
        }

        log.info(
                "\n[RID:{}] >>> {} {}\n" +
                "  time={} secure={} xfProto={} xfFor={}\n" +
                "  attr.loginUserId={}\n" +
                "  session.loginUserId={}\n" +
                "  cookie.remember.present={} len={}\n" +
                "  authByCookie={}\n",
                rid, method, full,
                Instant.now(), request.isSecure(), xfProto, xfFor,
                safeObj(attrUser),
                safeObj(sessionUser),
                hasRemember, rememberLen,
                authByCookie.map(UserId::value).orElse(null)
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            long ms = System.currentTimeMillis() - start;
            int status = response.getStatus();
            log.info("[RID:{}] <<< {} {} status={} {}ms\n", rid, method, uri, status, ms);
        }
    }

    private boolean hasCookie(Cookie[] cookies, String name) {
        if (cookies == null) return false;
        return Arrays.stream(cookies).anyMatch(c -> name.equals(c.getName()));
    }

    private Integer cookieLen(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(c -> c.getValue() == null ? 0 : c.getValue().length())
                .orElse(null);
    }

    private String safeObj(Object o) {
        if (o == null) return null;
        // UserId 的話印 value（避免 toString() 有包裝字）
        if (o instanceof UserId u) return u.value();
        return String.valueOf(o);
    }
}
