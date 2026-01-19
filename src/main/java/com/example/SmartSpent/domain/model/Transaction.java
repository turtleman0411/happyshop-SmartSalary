package com.example.SmartSpent.domain.model;

import java.time.LocalDateTime;

import com.example.SmartSpent.domain.value.TransactionId;

import jakarta.persistence.*;

/**
 * 💸 Transaction
 *
 * - 一筆已發生的消費事實
 * - 只能存在於某一個 BudgetMonth
 * - ❌ 不負責任何預算計算
 */
@Entity
@Table(name = "transactions")
class Transaction {

    /* ========== Identity ========== */
    @EmbeddedId
    private TransactionId id;

    /* ========== Ownership ========== */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_month_id", nullable = false)
    private BudgetMonth budgetMonth;

    /* ========== Classification ========== */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    /* ========== Fact ========== */
    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "image_path")
    private String imagePath;

    /* ========== Optional ========== */
    private String note;

    /* ========== JPA only ========== */
    protected Transaction() {}

    /* ========== Factory ========== */
    static Transaction create(
            CategoryType category,
            int amount,
            LocalDateTime date,
            String note
    ) {
        Transaction tx = new Transaction();
        tx.id = TransactionId.newId(); // 🔥 關鍵：Domain 產生
        tx.category = category;
        tx.amount = amount;
        tx.date = date;
        tx.note = note;
        return tx;
    }

    // ================= Domain Behavior =================

    void updateAmountNote(int amount, String note) {
    this.amount = amount;
    this.note = note;
}
    String replaceImagePath(String newImagePath) {
    String old = this.imagePath;
    this.imagePath = newImagePath;
    return old;
}


    /* ========== Aggregate binding ========== */
    void assignTo(BudgetMonth month) {
        this.budgetMonth = month;
    }

    void attachImage(String imagePath) {
    this.imagePath = imagePath;
}


    /* ========== Read-only ========== */

    public CategoryType getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TransactionId getId() {
        return id; 
    }

    public String getImagePath() {
    return imagePath;
}


    public String getNote() {
        return note;
    }
}
