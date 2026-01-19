package com.example.SmartSpent.presentation.dto.view;

import java.time.LocalDate;
import java.util.List;

public record TransactionDateGroupView(
    LocalDate date,
    List<TransactionItemView> transactions,
    int dayTotalAmount
) {}
