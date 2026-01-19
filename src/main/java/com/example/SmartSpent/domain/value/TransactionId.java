package com.example.SmartSpent.domain.value;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Transaction 的識別 Value Object
 * 由 Domain 產生（UUID），不依賴資料庫
 */
public final class TransactionId implements Serializable {

    private final String value;

    private TransactionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransactionId 不可為空");
        }
        this.value = value;
    }

    /** 建立全新 TransactionId（Domain 行為） */
    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID().toString());
    }

    /** 從持久化資料還原 */
    public static TransactionId of(String value) {
        return new TransactionId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionId)) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
