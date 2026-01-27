package com.example.SmartSpent.application.security;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "remember_me_token")
public class RememberMeToken {

    @Id
    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected RememberMeToken() {}

    public RememberMeToken(String tokenHash, String userId, LocalDateTime expireAt, LocalDateTime createdAt) {
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expireAt = expireAt;
        this.createdAt = createdAt;
    }

    public String getTokenHash() { return tokenHash; }
    public String getUserId() { return userId; }
    public LocalDateTime getExpireAt() { return expireAt; }

    public boolean isExpired(LocalDateTime now) {
        return !expireAt.isAfter(now);
    }
}

