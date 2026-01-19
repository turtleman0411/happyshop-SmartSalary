package com.example.SmartSpent.presentation.dto.view;

import java.time.LocalDate;

public record TransactionItemView(
    String transactionId,
    LocalDate date,
    String categoryName,
    String categoryDisplayName,
    int amount,
    String note,
    String imageUrl
) {}

