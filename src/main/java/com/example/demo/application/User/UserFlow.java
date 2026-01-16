package com.example.demo.application.User;

import java.time.YearMonth;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.domain.model.CategoryType;
import com.example.demo.domain.value.BudgetMonthId;
import com.example.demo.domain.value.IncomeAmount;
import com.example.demo.domain.value.UserId;


@Service

public class UserFlow {

    private final UserRegisterService registerService;
    private final LoginService loginService;
    private final MonthService monthService;

    public UserFlow(
            UserRegisterService registerService,
            LoginService loginService,
            MonthService monthService
    ) {
        this.registerService = registerService;
        this.loginService = loginService;
        this.monthService = monthService;
    }

    /* ===== 對 Controller 開放的入口 ===== */

    public UserId register(String rawAccount, String password) {
        return registerService.register(rawAccount, password);
    }

    public UserId login(String account, String password) {
        return loginService.login(account, password);
    }

    public BudgetMonthId configureMonthlyBudget(
            UserId userId,
            YearMonth month,
            Map<CategoryType, Integer> percents
    ) {
        System.out.println("🔥 Domain 收到 percents = " + percents);

        return monthService.configureMonthlyBudget(
                userId,
                month,
                percents
        );
    }
    
    public void updateIncome(
            UserId userId,
            YearMonth month,
            int income
    ) {
        // 目前最小可用版本：
        // 👉 先只負責「流程存在」，資料來源之後再抽 DB / Domain

        System.out.println("💰 update income");
        System.out.println("userId = " + userId);
        System.out.println("month  = " + month);
        System.out.println("income = " + income);
        monthService.updateIncome(
        userId,
        month,
        IncomeAmount.of(income)
    );
    }

    
}
