package com.example.demo.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

/**
 * BudgetMonth 的識別型 Value Object
 *
 * - 用來識別一個 BudgetMonth Aggregate
 * - 不承載任何業務屬性
 * - 與 UserId 採用相同設計，降低學習成本
 */
@Embeddable
public class BudgetMonthId implements Serializable {

    @Column(name = "budget_month_id", nullable = false, updatable = false)
    private String value;


    /* ===== JPA only ===== */
    protected BudgetMonthId() {}

    private BudgetMonthId(String value) {
        this.value = value;
    }

    /** 建立新識別（與 UserId.newId() 對齊） */
    public static BudgetMonthId newId() {
        return new BudgetMonthId(UUID.randomUUID().toString());
    }

    /** 讀取值（僅供識別 / 查詢 / 協調流程使用） */
    public String value() {
        return value;
    }

    /* ===== Identity VO contract ===== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BudgetMonthId)) return false;
        BudgetMonthId other = (BudgetMonthId) o;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
