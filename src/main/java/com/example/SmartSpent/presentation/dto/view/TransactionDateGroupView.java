package com.example.SmartSpent.presentation.dto.view;

import java.time.LocalDateTime;
import java.util.List;

public record TransactionDateGroupView(
    LocalDateTime date,
    List<TransactionItemView> transactions,
    int dayTotalAmount
) {}
