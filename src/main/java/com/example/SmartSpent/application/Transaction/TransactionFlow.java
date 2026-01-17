package com.example.SmartSpent.application.Transaction;

import java.time.YearMonth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.infrastructure.storage.ImageStorage;
import com.example.SmartSpent.presentation.dto.request.AddTransactionRequest;

@Component
@Transactional
public class TransactionFlow {

    private final AddTransactionService addTransactionService;
     private final BudgetMonthRepository budgetMonthRepository;
    private final DeleteTransactionService deleteTransactionService;
    private final ImageStorage imageStorage;

    public TransactionFlow(
            AddTransactionService addTransactionService,
            BudgetMonthRepository budgetMonthRepository,
            DeleteTransactionService deleteTransactionService,
            ImageStorage imageStorage
    ) {
        this.addTransactionService = addTransactionService;
        this.deleteTransactionService = deleteTransactionService;
        this.budgetMonthRepository = budgetMonthRepository;
        this.imageStorage = imageStorage;
    }


    /**
     * 使用者在指定月份新增交易（流程封裝）
     */
    public void addTransaction(
            UserId userId,
            YearMonth month,
            AddTransactionRequest request
    ) {
        // 1️⃣ 先建立交易（Domain）
        addTransactionService.addTransaction(
                userId,
                month,
                CategoryType.valueOf(request.categoryName()),
                request.date(),
                request.amount(),
                request.note()
        );

        // 2️⃣ 沒圖片就結束
        if (request.image() == null || request.image().isEmpty()) {
            return;
        }
        BudgetMonth budgetMonth =
            budgetMonthRepository
                    .findByUserIdAndMonth(userId, month)
                    .orElseThrow();
        TransactionId txId = budgetMonth.getLastTransactionId();

        // 3️⃣ 存圖片（技術）
        String imagePath =
                imageStorage.save(
                        month,
                        txId,
                        request.date(),
                        request.image()
                );

        budgetMonth.attachTransactionImage(txId, imagePath);
    }


    public void deleteTransaction(
            UserId userId,
            YearMonth month,
            TransactionId transactionId
    ) {
        deleteTransactionService.delete(
                userId,
                month,
                transactionId
        );
    }


}
