package com.example.SmartSpent.application.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SmartSpent.domain.model.RememberMeToken;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.RememberMeTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class RememberMeService {

    public static final String COOKIE_NAME = "HAPPYSHOP_REMEMBER";
    public static final int REMEMBER_DAYS = 30;
    public static final int SESSION_DAYS = 1; // 沒勾選：DB 留短期即可（例如 1 天）

    private final RememberMeTokenRepository repo;
    private final SecureRandom random = new SecureRandom();

    public RememberMeService(RememberMeTokenRepository repo) {
        this.repo = repo;
    }

    /* ===============================
       對外唯一入口（給 Interceptor / Controller fallback）
       =============================== */
    @Transactional(readOnly = true)
    public Optional<UserId> authenticate(HttpServletRequest request) {
        String rawToken = readCookie(request, COOKIE_NAME);
        if (rawToken == null || rawToken.isBlank()) return Optional.empty();

        String tokenHash = sha256Hex(rawToken);
        LocalDateTime now = LocalDateTime.now();

        return repo.findByTokenHash(tokenHash)
                .filter(t -> !t.isExpired(now))
                .map(t -> UserId.from(t.getUserId())); // ✅ DB 存的是 userId.value()
    }

    /** ✅ 登入成功後呼叫：是否持久化由 rememberMe 決定 */
    @Transactional
    public void issue(
            UserId userId,
            HttpServletRequest request,
            HttpServletResponse response,
            boolean rememberMe
    ) {
        // ✅ 只存「純 value」，避免 toString() 超長炸 DB
        String userIdValue = userId.value();

        // 這個使用者只留一個有效 token（避免堆積）
        repo.deleteByUserId(userIdValue);

        String rawToken = generateRawToken();
        String tokenHash = sha256Hex(rawToken);

        LocalDateTime now = LocalDateTime.now();
        int days = rememberMe ? REMEMBER_DAYS : SESSION_DAYS;
        LocalDateTime expireAt = now.plusDays(days);
        System.out.println("userId.value()=" + userId.value());
System.out.println("len=" + userId.value().length());
        repo.save(new RememberMeToken(tokenHash, userIdValue, expireAt, now));

        boolean isHttps = isHttps(request);

        Cookie cookie = new Cookie(COOKIE_NAME, rawToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttps);
        cookie.setPath("/");

        // ✅ 關鍵：沒勾選 → Session Cookie（關瀏覽器消失）
        cookie.setMaxAge(rememberMe ? (REMEMBER_DAYS * 24 * 60 * 60) : -1);

        response.addCookie(cookie);
    }

    @Transactional
    public void clear(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = readCookie(request, COOKIE_NAME);
        if (rawToken != null && !rawToken.isBlank()) {
            repo.deleteById(sha256Hex(rawToken));
        }

        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    // -------- helpers --------

    /** ✅ Railway/反代下，request.isSecure() 可能永遠 false，所以讀 X-Forwarded-Proto */
    private boolean isHttps(HttpServletRequest request) {
        String xfProto = request.getHeader("X-Forwarded-Proto");
        if (xfProto != null) return "https".equalsIgnoreCase(xfProto);
        return request.isSecure();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("sha256 failed", e);
        }
    }
}
