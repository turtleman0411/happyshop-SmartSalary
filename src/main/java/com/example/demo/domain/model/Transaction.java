package com.example.demo.domain.model;

import java.time.LocalDate;

import com.example.demo.domain.value.TransactionId;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private LocalDate date;

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
            LocalDate date,
            String note
    ) {
        Transaction tx = new Transaction();
        tx.category = category;
        tx.amount = amount;
        tx.date = date;
        tx.note = note;
        return tx;
    }

    // ================= Domain Behavior =================

    void update(LocalDate date, int amount, String note) {
        this.date = date;
        this.amount = amount;
        this.note = note;
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

    public LocalDate getDate() {
        return date;
    }

    public TransactionId getId() {
    return TransactionId.of(this.id);
}

    public String getImagePath() {
    return imagePath;
}


    public String getNote() {
        return note;
    }
}
