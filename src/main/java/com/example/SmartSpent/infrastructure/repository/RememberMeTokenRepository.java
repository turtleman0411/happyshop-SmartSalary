package com.example.SmartSpent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SmartSpent.domain.model.RememberMeToken;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {

    Optional<RememberMeToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);

    void deleteByExpireAtBefore(LocalDateTime time);
}
