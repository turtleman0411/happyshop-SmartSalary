package com.example.demo.application.Transaction;

import com.example.demo.presentation.dto.view.TransactionPageView;

public record TransactionPageFlowResult(
        TransactionPageView view,
        String themeClass
) {}
