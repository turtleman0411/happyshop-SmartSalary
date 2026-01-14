package com.example.demo.domain.value;

import com.example.demo.application.exception.PasswordInvalidException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String hash;

    protected Password() {} // JPA only

    private Password(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Password hash 不可為 null");
        }
        this.hash = hash;
    }

    // ⭐ 方法名不變
    public static Password fromRaw(String raw, PasswordHasher hasher) {
        if (raw == null || raw.length() < 7) {
            throw new PasswordInvalidException("密碼長度不足 需大於五位數");
        }
        return new Password(hasher.hash(raw));
    }

    // ⭐ 方法名不變
    public boolean matches(String raw, PasswordHasher hasher) {
        return hasher.matches(raw, this.hash);
    }

    // VO 標準
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;
        Password other = (Password) o;
        return hash.equals(other.hash);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    // package-private 給 application 用
    String getHash() {
        return hash;
    }
}

