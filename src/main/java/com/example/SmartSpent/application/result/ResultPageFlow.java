package com.example.SmartSpent.application.result;

import java.time.YearMonth;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.query.ResultPageQueryService;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.BudgetMonthId;
import com.example.SmartSpent.domain.value.IncomeAmount;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.view.ResultPageView;

@Service
public class ResultPageFlow {

    private final ResultPageQueryService queryService;
    private final MonthService monthService;
    public ResultPageFlow(ResultPageQueryService queryService, MonthService monthService) {
        this.queryService = queryService;
        this.monthService = monthService;
    }

    public  ResultPageView getResultPage(UserId userId, YearMonth month) {

        ResultPageView view =
                queryService.getResultPage(userId, month);

        return view;
    }


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
    YearMonth targetMonth = (month != null)
            ? month
            : YearMonth.now(); // 或之後換成從 Session / Context 拿

    monthService.updateIncome(
            userId,
            targetMonth,
            IncomeAmount.of(income)
    );
    }

    public void reset(UserId userId, String month) {
        YearMonth ym = YearMonth.parse(month);
        monthService.reset(userId, ym);
    }
}
