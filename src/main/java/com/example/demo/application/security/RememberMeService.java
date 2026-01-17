package com.example.demo.application.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.RememberMeToken;
import com.example.demo.domain.value.UserId;
import com.example.demo.infrastructure.repository.RememberMeTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class RememberMeService {

    // ✅ 你可以改名字
    public static final String COOKIE_NAME = "HAPPYSHOP_REMEMBER";
    public static final int COOKIE_DAYS = 30;

    private final RememberMeTokenRepository repo;
    private final SecureRandom random = new SecureRandom();

    public RememberMeService(RememberMeTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * 登入成功後呼叫：發 cookie + 存 DB
     */
    @Transactional
    public void issue(UserId userId, HttpServletResponse response, boolean isHttps) {
        // 同一個使用者只保留一個 token（簡單策略）
        repo.deleteByUserId(userId.toString());

        String rawToken = generateRawToken();        // 給 cookie 的
        String tokenHash = sha256Hex(rawToken);      // 存 DB 的

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(COOKIE_DAYS);

        repo.save(new RememberMeToken(tokenHash, userId.toString(), expireAt, now));

        Cookie cookie = new Cookie(COOKIE_NAME, rawToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttps); // Railway https => true
        cookie.setPath("/");       // 讓全站都吃得到
        cookie.setMaxAge(COOKIE_DAYS * 24 * 60 * 60);

        // SameSite 用 response header 補（Cookie API 沒有 SameSite）
        response.addCookie(cookie);
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax%s",
                        COOKIE_NAME,
                        rawToken,
                        COOKIE_DAYS * 24 * 60 * 60,
                        isHttps ? "; Secure" : ""
                )
        );
    }

    /**
     * 進站自動登入：用 request cookie 去 DB 換回 userId
     */
    @Transactional(readOnly = true)
    public Optional<UserId> resolveUser(HttpServletRequest request) {
        String rawToken = readCookie(request, COOKIE_NAME);
        if (rawToken == null || rawToken.isBlank()) return Optional.empty();

        String tokenHash = sha256Hex(rawToken);
        var now = LocalDateTime.now();

        return repo.findByTokenHash(tokenHash)
                .filter(t -> !t.isExpired(now))
                .map(t -> UserId.from(t.getUserId())); // ✅ 若你沒有 from()，改成你自己的建構方式
    }

    /**
     * 登出：清 DB + 清 cookie
     */
    @Transactional
    public void clear(HttpServletRequest request, HttpServletResponse response, boolean isHttps) {
        String rawToken = readCookie(request, COOKIE_NAME);
        if (rawToken != null && !rawToken.isBlank()) {
            repo.deleteById(sha256Hex(rawToken));
        }

        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttps);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        response.addHeader("Set-Cookie",
                String.format("%s=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax%s",
                        COOKIE_NAME,
                        isHttps ? "; Secure" : ""
                )
        );
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
