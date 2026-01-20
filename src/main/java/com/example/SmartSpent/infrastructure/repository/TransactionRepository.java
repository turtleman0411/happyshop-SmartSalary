package com.example.SmartSpent.infrastructure.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.BudgetMonthId;
import com.example.SmartSpent.infrastructure.repository.projection.CategorySpentRow;
import com.example.SmartSpent.infrastructure.repository.projection.TransactionRow;

public interface TransactionRepository
        extends JpaRepository<BudgetMonth, BudgetMonthId> {

    /* =========================
     * 1️⃣ 本月總消費
     * ========================= */
    @Query("""
        select coalesce(sum(t.amount), 0)
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
    """)
    int sumMonthlySpent(String userId, YearMonth month);

    /* =========================
     * 2️⃣ 分類消費彙總
     * ========================= */
    @Query("""
        select
            t.category as category,
            coalesce(sum(t.amount), 0) as spentAmount
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
        group by t.category
    """)
    List<CategorySpentRow> sumCategorySpent(String userId, YearMonth month);

    /* =========================
     * 3️⃣ 交易清單：查全部（✅ 不用 null）
     * ========================= */
    @Query("""
        select
            t.id.value as transactionId,
            t.date as date,
            t.category as category,
            t.amount as amount,
            t.note as note,
            t.imagePath as imagePath
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
        order by t.date desc, t.id.value desc
    """)
    List<TransactionRow> findAllTransactions(String userId, YearMonth month);

    /* =========================
     * 4️⃣ 交易清單：查分類（可留著，未來若要後端導頁才用）
     * ========================= */
    @Query("""
        select
            t.id.value as transactionId,
            t.date as date,
            t.category as category,
            t.amount as amount,
            t.note as note,
            t.imagePath as imagePath
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
          and t.category = :category
        order by t.date desc, t.id.value desc
    """)
    List<TransactionRow> findTransactionsByCategory(String userId, YearMonth month, CategoryType category);

    /* =========================
     * 5️⃣ 最近 3 筆（本月，不受分類影響）
     * ========================= */
    @Query("""
        select
            t.id.value as transactionId,
            t.date as date,
            t.category as category,
            t.amount as amount,
            t.note as note,
            t.imagePath as imagePath
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
        order by t.date desc, t.id.value desc
    """)
    List<TransactionRow> findRecentTransactions(String userId, YearMonth month, Pageable pageable);
}
