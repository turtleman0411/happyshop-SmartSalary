package com.example.demo.infrastructure.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.model.BudgetMonth;
import com.example.demo.domain.value.BudgetMonthId;
import com.example.demo.infrastructure.repository.projection.CategoryAllocationRow;

public interface CategoryAllocationRepository
        extends JpaRepository<BudgetMonth, BudgetMonthId> {

    @Query("""
        select
            ca.category as category,
            ca.percent as percent
        from BudgetMonth bm
        join bm.allocations ca
        where bm.userId.value = :userId
          and bm.month = :month
        order by ca.id asc
    """)
    List<CategoryAllocationRow> findCategoryAllocations(
            String userId,
            YearMonth month
    );
}
