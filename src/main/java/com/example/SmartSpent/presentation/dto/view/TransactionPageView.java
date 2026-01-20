package com.example.SmartSpent.presentation.dto.view;

import java.time.YearMonth;
import java.util.List;

public record TransactionPageView(
        YearMonth month,
        List<TransactionDateGroupView> groupedTransactions,
        List<TransactionItemView> recentTransactions,
        List<CategoryOptionView> categoryOptions
) {
    public static TransactionPageView of(
            YearMonth month,
            List<TransactionDateGroupView> groupedTransactions,
            List<TransactionItemView> recentTransactions,
            List<CategoryOptionView> categoryOptions
    ) {
        return new TransactionPageView(
                month,
                groupedTransactions,
                recentTransactions,
                categoryOptions
        );
    }
}
