package com.example.SmartSpent.application.User;

import java.time.YearMonth;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.security.RememberMeTokenService;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.BudgetMonthId;
import com.example.SmartSpent.domain.value.IncomeAmount;
import com.example.SmartSpent.domain.value.UserId;

@Service
public class UserFlow {

    private final UserRegisterService registerService;
    private final LoginService loginService;
    private final MonthService monthService;
    private final RememberMeTokenService rememberMeTokenService;

    public UserFlow(
            UserRegisterService registerService,
            LoginService loginService,
            MonthService monthService,
            RememberMeTokenService rememberMeTokenService
    ) {
        this.registerService = registerService;
        this.loginService = loginService;
        this.monthService = monthService;
        this.rememberMeTokenService = rememberMeTokenService;
    }

    /* =========================
     * 對 Controller 的入口
     * ========================= */

    public UserId register(String rawAccount, String password) {
        return registerService.register(rawAccount, password);
    }

    /**
     * 帳密登入（純登入，不處理 remember-me）
     */
    public UserId login(String account, String password) {
        return loginService.login(account, password);
    }

    /**
     * 登入成功後發行 remember-me token
     * - 回傳 raw token（Controller 負責設 cookie）
     */
    public String issueRememberMeToken(UserId userId, int days) {
        return rememberMeTokenService.issueToken(userId, days);
    }

    /* =========================
     * 對 Interceptor 的入口
     * ========================= */

    /**
     * remember-me 自動登入（用 raw token 換回 UserId）
     * - 成功回傳 UserId
     * - 失敗回傳 null
     */
    public UserId loginWithRememberMeToken(String rawToken) {
        return rememberMeTokenService.verifyToken(rawToken);
    }

    /* =========================
     * 其他業務流程
     * ========================= */

    public BudgetMonthId configureMonthlyBudget(
            UserId userId,
            YearMonth month,
            Map<CategoryType, Integer> percents
    ) {
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
        monthService.updateIncome(
                userId,
                month,
                IncomeAmount.of(income)
        );
    }
}
