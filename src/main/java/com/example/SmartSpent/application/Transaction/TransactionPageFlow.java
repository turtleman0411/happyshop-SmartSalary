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

    public TransactionPageView getTransactionPage(
            UserId userId,
            YearMonth month,
            String category
    ) {

        TransactionPageView view =
                queryService.getTransactionPage(userId, month, category);

       

        return view;
    }
}

