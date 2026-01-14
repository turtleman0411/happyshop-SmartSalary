package com.example.demo.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;


@Embeddable
public class UserId implements Serializable {

    @Column(name = "user_id", nullable = false, updatable = false)
    private String value;

    protected UserId() {} // JPA

    private UserId(String value) {
        this.value = value;
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID().toString());
    }

    public String value() {
        return value;
    }

    /* =========================
     * equals / hashCode（必要）
     * ========================= */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId other = (UserId) o;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

