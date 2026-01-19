package com.example.SmartSpent.application.Transaction;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartSpent.application.query.TransactionPageQueryService;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TransactionPageFlow {

    private final TransactionPageQueryService queryService;
    private final TransactionUpdateService updateService;
    public TransactionPageFlow(TransactionPageQueryService queryService,TransactionUpdateService updateService) {
        this.queryService = queryService;
        this.updateService = updateService;
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

    public TransactionId updateTransaction(
        UserId userId,
        YearMonth month,
        TransactionId transactionId,
        int amount,
        String note,
        MultipartFile image
) {
    return updateService.update(
            userId, month, transactionId, amount, note, image
    );
}

}

