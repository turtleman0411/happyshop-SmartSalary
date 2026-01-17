package com.example.demo.application.security;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {

    Optional<RememberMeToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);

    void deleteByExpireAtBefore(LocalDateTime time);
}
