package com.example.SmartSpent.presentation.dto.view;

import java.time.LocalDateTime;

public record TransactionItemView(
    String transactionId,
    LocalDateTime date,
    String categoryName,
    String categoryDisplayName,
    int amount,
    String note,
    String imageUrl
) {}

