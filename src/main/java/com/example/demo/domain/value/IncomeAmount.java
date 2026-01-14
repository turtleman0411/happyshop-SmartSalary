package com.example.demo.domain.value;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public final class IncomeAmount {

    private int value;

    private IncomeAmount(int value) {
        this.value = value;
    }

    // 🔒 給 JPA 用
    protected IncomeAmount() {
    }

    public static IncomeAmount of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("income 不可小於 0");
        }
        return new IncomeAmount(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncomeAmount)) return false;
        IncomeAmount that = (IncomeAmount) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
