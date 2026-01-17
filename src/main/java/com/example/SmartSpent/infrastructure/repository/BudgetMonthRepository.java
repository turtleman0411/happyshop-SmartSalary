package com.example.SmartSpent.infrastructure.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.value.BudgetMonthId;
import com.example.SmartSpent.domain.value.UserId;

public interface BudgetMonthRepository
        extends JpaRepository<BudgetMonth, BudgetMonthId> {

    Optional<BudgetMonth> findByUserIdAndMonth(
            UserId userId,
            YearMonth month
    );

    boolean existsByUserIdAndMonth(
            UserId userId,
            YearMonth month
    );
}
