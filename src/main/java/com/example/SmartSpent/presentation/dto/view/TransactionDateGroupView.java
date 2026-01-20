package com.example.SmartSpent.presentation.dto.view;

import java.time.LocalDate;
import java.util.List;

public record TransactionDateGroupView(
        LocalDate date,
        int dayTotalAmount,
        List<TransactionItemView> transactions
) {
    public static TransactionDateGroupView of(
            LocalDate date,
            int dayTotalAmount,
            List<TransactionItemView> transactions
    ) {
        return new TransactionDateGroupView(date, dayTotalAmount, transactions);
    }
}
