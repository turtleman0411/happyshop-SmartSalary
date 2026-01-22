package com.example.SmartSpent.application.Transaction;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.value.TransactionDeletion;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.infrastructure.storage.ImageStorage;

@Service
@Transactional
class DeleteTransactionService {

    private final BudgetMonthRepository budgetMonthRepository;
    private final ImageStorage imageStorage;

    public DeleteTransactionService(
            BudgetMonthRepository budgetMonthRepository,
            ImageStorage imageStorage
    ) {
        this.budgetMonthRepository = budgetMonthRepository;
        this.imageStorage = imageStorage;
    }

    public void delete(
            UserId userId,
            YearMonth month,
            TransactionId transactionId
    ) {
        BudgetMonth budgetMonth =
                budgetMonthRepository
                        .findByUserIdAndMonth(userId, month)
                        .orElseThrow(() -> new IllegalStateException("月份不存在"));

        
        TransactionDeletion deletion =
                budgetMonth.deleteTransaction(transactionId);

        imageStorage.delete(deletion.imagePath());
    }
}

