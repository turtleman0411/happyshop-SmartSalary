package com.example.SmartSpent.application.BudgetMonth;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;

@Service
@Transactional
public class ResetBudgetMonthService {

    private final BudgetMonthRepository repository;

    public ResetBudgetMonthService(BudgetMonthRepository repository) {
        this.repository = repository;
    }

    public void reset(UserId userId, YearMonth month) {
        BudgetMonth budgetMonth = repository
            .findByUserIdAndMonth(userId, month)
            .orElseThrow(() -> new IllegalStateException("月份不存在"));

        budgetMonth.resetAllocations();
    }
}

