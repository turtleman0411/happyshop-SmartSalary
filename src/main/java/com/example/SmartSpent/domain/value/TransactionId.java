package com.example.SmartSpent.domain.value;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public  class TransactionId  {

    @Column(name = "id", nullable = false, updatable = false)
    private  String value;
    protected TransactionId() {
        this.value = null; 
    }

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
