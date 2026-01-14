package com.example.demo.application.BudgetMonth;

import java.time.YearMonth;

import org.springframework.stereotype.Component;

import com.example.demo.domain.value.UserId;

@Component
public class BudgetMonthResetFlow{

    private final ResetBudgetMonthService service;

    public BudgetMonthResetFlow(ResetBudgetMonthService service) {
        this.service = service;
    }

    public void reset(UserId userId, String month) {
        YearMonth ym = YearMonth.parse(month);
        service.reset(userId, ym);
    }
}

