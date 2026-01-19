package com.example.SmartSpent.infrastructure.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.BudgetMonthId;
import com.example.SmartSpent.infrastructure.repository.projection.CategorySpentRow;
import com.example.SmartSpent.infrastructure.repository.projection.TransactionRow;

public interface TransactionRepository
        extends JpaRepository<BudgetMonth, BudgetMonthId> {

    /**
     * ⭐ 全月總消費（唯一事實來源）
     */
    @Query("""
        select coalesce(sum(t.amount), 0)
        from BudgetMonth bm
        join bm.transactions t
        where bm.userId.value = :userId
          and bm.month = :month
    """)
    int sumMonthlySpent(String userId, YearMonth month);

    /**
     * ⭐ 分類消費彙總（歷史事實）
     */
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
    
    /**
     * ⭐ 交易查詢（唯一來源）
     * - selectedCategory = null / "ALL" → 查全部
     * - selectedCategory = "RENT" / "FOOD" → 查指定分類
     */


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
      and (
            :category is null
            or t.category = :category
      )
    order by t.date desc
""")
List<TransactionRow> findTransactions(
        String userId,
        YearMonth month,
        CategoryType category
);
/**
 * ⭐ 最近 3 筆交易（本月、不受分類影響）
 */
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
List<TransactionRow> findRecentTransactions(
        String userId,
        YearMonth month,
        org.springframework.data.domain.Pageable pageable
);


}
