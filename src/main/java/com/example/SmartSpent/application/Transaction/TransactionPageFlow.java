package com.example.SmartSpent.application.Transaction;

import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.query.TransactionPageQueryService;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

@Service
public class TransactionPageFlow {

    private final TransactionPageQueryService queryService;

    public TransactionPageFlow(TransactionPageQueryService queryService) {
        this.queryService = queryService;
    }

    public TransactionPageFlowResult getTransactionPage(
            UserId userId,
            YearMonth month,
            String category
    ) {

        TransactionPageView view =
                queryService.getTransactionPage(userId, month, category);

        String themeClass = resolveMonthTheme(month);

        return new TransactionPageFlowResult(view, themeClass);
    }

    private String resolveMonthTheme(YearMonth month) {
        int m = month.getMonthValue();

        if (m == 12) return "theme-winter theme-xmas";
        if (m == 1 || m == 2) return "theme-winter";
        if (m >= 3 && m <= 5) return "theme-spring";
        if (m >= 6 && m <= 8) return "theme-summer";
        return "theme-autumn";
    }
}

