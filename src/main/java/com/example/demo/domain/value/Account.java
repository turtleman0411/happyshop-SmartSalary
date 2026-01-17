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
    public static Account of(String raw) {

        if (raw == null) {
            throw new IllegalArgumentException("帳號不可以為空");
        }

        // ✅ 移除常見不可見空白（瀏覽器 autofill 常見）
        String normalized = raw
                .replace('\u00A0', ' ')   // non-breaking space
                .replace('\u200B', ' ')   // zero-width space
                .trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("帳號不可以為空");
        }

        return new Account(normalized);
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
