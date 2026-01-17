package com.example.SmartSpent.domain.value;

import java.io.Serializable;
import java.util.Objects;

/**
 * Transaction 的識別 Value Object
 * 由資料庫產生（IDENTITY），Domain 只負責使用
 */
public final class TransactionId implements Serializable {

    private final Long value;

    private TransactionId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId 不可為 null");
        }
        this.value = value;
    }

    public static TransactionId of(Long value) {
        return new TransactionId(value);
    }

    public Long value() {
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
        return value.toString();
    }
}
