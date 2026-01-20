package com.example.SmartSpent.domain.value;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TransactionId implements Serializable {

    @Column(name = "id", nullable = false, updatable = false)
    private String value;

    protected TransactionId() {
        this.value = null;
    }

    private TransactionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransactionId 不可為空");
        }
        // ✅ 強制 UUID 格式（避免前端亂傳）
        try {
            UUID.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid TransactionId UUID: " + value);
        }
        this.value = value;
    }

    /** 建立全新 TransactionId（Domain 行為） */
    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID().toString());
    }

    /** 前端字串 → VO */
    public static TransactionId of(String raw) {
        return new TransactionId(raw);
    }

    public String value() {
        return value;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionId)) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(value, that.value);
    }

    @Override public int hashCode() {
        return Objects.hash(value);
    }

    @Override public String toString() {
        return value;
    }
}
