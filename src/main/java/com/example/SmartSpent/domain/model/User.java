package com.example.SmartSpent.domain.model;

import java.time.LocalDateTime;

import com.example.SmartSpent.domain.value.Account;
import com.example.SmartSpent.domain.value.Password;
import com.example.SmartSpent.domain.value.PasswordHasher;
import com.example.SmartSpent.domain.value.UserId;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    private static final int MAX_LOGIN_FAILURE = 3;

    /* ========== Identity ========== */
    @EmbeddedId
    private UserId id;

    /* ========== Credentials ========== */
    @Embedded
    private Account account;

    @Embedded
    private Password password;

    /* ========== Login State ========== */
    private int loginFailCount;

    private boolean locked;

    /* ========== Metadata ========== */
    private LocalDateTime createdAt;

    /* ========== JPA only ========== */
    protected User() {}

    /* ========== Factory ========== */
    private User(
            Account account,
            Password password
    ) {
        this.id = UserId.newId();
        this.account = account;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.loginFailCount = 0;
        this.locked = false;
    }

    public static User create(
            Account account,
            Password password
    ) {
        return new User(
                account,
                password
        );
    }

    /* ========== Domain Behaviors ========== */

    public LocalDateTime getCreatedAt(){return createdAt;}

    public UserId getId() {
        return id;
    }

    public boolean isLocked() {
        return locked;
    }

    public int remainAttempts() {
        return Math.max(
                MAX_LOGIN_FAILURE - loginFailCount,
                0
        );
    }

    public void recordLoginFailure() {
        this.loginFailCount++;

        if (this.loginFailCount >= MAX_LOGIN_FAILURE) {
            this.locked = true;
        }
    }

    public void resetLoginFailure() {
        this.loginFailCount = 0;
        this.locked = false;
    }

    public boolean matchesPassword(
        String rawPassword,
        PasswordHasher hasher
    ) {
    return password.matches(rawPassword, hasher);
    }

    // public void changePassword(Password newPassword) {
    //     this.password = newPassword;
    //     this.resetLoginFailure();
    // }
}
