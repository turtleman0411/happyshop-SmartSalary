package com.example.demo.infrastructure.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.model.BudgetMonth;
import com.example.demo.domain.value.BudgetMonthId;
import com.example.demo.domain.value.UserId;

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
