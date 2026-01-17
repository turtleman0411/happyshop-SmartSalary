package com.example.SmartSpent.application.Transaction;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;

import jakarta.transaction.Transactional;
@Service
@Transactional
class AddTransactionService {

    private final BudgetMonthRepository budgetMonthRepository;

    AddTransactionService(BudgetMonthRepository budgetMonthRepository) {
        this.budgetMonthRepository = budgetMonthRepository;
    }

    void addTransaction(
            UserId userId,
            YearMonth month,
            CategoryType category,
            LocalDate date,
            int amount,
            String note
    ) {
        BudgetMonth budgetMonth =
                budgetMonthRepository
                        .findByUserIdAndMonth(userId, month)
                        .orElseThrow(() ->
                                new IllegalStateException("本月尚未設定預算")
                        );

        budgetMonth.addTransaction(
                category,
                amount,
                date,
                note
        );
        // ⭐ Dirty Checking 會存
    }
}
