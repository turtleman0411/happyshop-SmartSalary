package com.example.demo.application.security;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.value.UserId;

@Service
public class RememberMeTokenService {

    private final RememberMeTokenRepository repository;

    public RememberMeTokenService(RememberMeTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * 發行 remember-me token
     * - 回傳 raw token（給 cookie 用）
     * - DB 只存 token hash
     */
    public String issueToken(UserId userId, int days) {

        // 1️⃣ 產生 raw token（只給瀏覽器）
        String rawToken = UUID.randomUUID()
                .toString()
                .replace("-", "");

        // 2️⃣ hash 後才存 DB
        String tokenHash = TokenHasher.sha256(rawToken);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(days);

        // 3️⃣ 存入資料庫
        RememberMeToken token = new RememberMeToken(
                tokenHash,
                userId.value(),
                expireAt,
                now
        );

        repository.save(token);

        // 4️⃣ 回傳 raw token
        return rawToken;
    }

    /**
     * 驗證 remember-me token（自動登入用）
     * - 成功回傳 UserId
     * - 失敗回傳 null
     */
   public UserId verifyToken(String rawToken) {

    String tokenHash = TokenHasher.sha256(rawToken);

    return repository.findById(tokenHash)
            .filter(token -> !token.isExpired(LocalDateTime.now()))
            .map(token -> UserId.from(token.getUserId()))
            .orElse(null);
}
}
