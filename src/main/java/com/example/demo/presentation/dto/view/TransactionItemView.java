package com.example.demo.presentation.dto.view;

import java.time.LocalDate;

public record TransactionItemView(
    Long transactionId,
    LocalDate date,
    String categoryName,
    String categoryDisplayName,
    int amount,
    String note,
    String imageUrl
) {}

