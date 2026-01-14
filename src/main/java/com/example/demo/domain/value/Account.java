package com.example.demo.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Account {

    @Column(name = "account", nullable = false, unique = true)
    private String value;

    /** JPA only */
    protected Account() {}

    private Account(String value) {
        this.value = value;
    }

    /** Factory：建立帳號（格式驗證在這裡） */
    public static Account of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("帳號不可以為空");
        }
        return new Account(value.trim());
    }

    /** Read-only（可顯示） */
    public String value() {
        return value;
    }

    /* ===== Value Object contract ===== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account other = (Account) o;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
