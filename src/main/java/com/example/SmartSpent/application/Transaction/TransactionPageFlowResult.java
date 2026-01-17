package com.example.SmartSpent.application.Transaction;

import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

public record TransactionPageFlowResult(
        TransactionPageView view,
        String themeClass
) {}
