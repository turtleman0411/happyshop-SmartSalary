package com.example.SmartSpent.application.Transaction;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.component.ImageStorage;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.presentation.dto.request.AddTransactionRequest;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

@Component
public class TransactionFlow {

    private final AddTransactionService addTransactionService;
    private final DeleteTransactionService deleteTransactionService;
    private final TransactionUpdateService updateService;
    private final TransactionPageQueryService queryService;
    private final BudgetMonthRepository budgetMonthRepository;
    private final ImageStorage imageStorage;

    public TransactionFlow(
            AddTransactionService addTransactionService,
            DeleteTransactionService deleteTransactionService,
            TransactionUpdateService transactionUpdateService,
            BudgetMonthRepository budgetMonthRepository,
            ImageStorage imageStorage,
            TransactionPageQueryService queryService
    ) {
        this.addTransactionService = addTransactionService;
        this.deleteTransactionService = deleteTransactionService;
        this.updateService = transactionUpdateService;
        this.budgetMonthRepository = budgetMonthRepository;
        this.imageStorage = imageStorage;
        this.queryService = queryService;
    }

    /** 新增交易 */
    @Transactional
    public void addTransaction(UserId userId, YearMonth month, AddTransactionRequest request) {


        TransactionId txId = addTransactionService.addTransaction(
                userId,
                month,
                CategoryType.valueOf(request.categoryName()),
                request.date(),
                request.amount(),
                request.note()
        );

        if (request.image() == null || request.image().isEmpty()) {
            return;
        }

        BudgetMonth bm = budgetMonthRepository
                .findByUserIdAndMonth(userId, month)
                .orElseThrow();

        String newPath = imageStorage.save(month, txId, request.date(), request.image());
        String oldPath = bm.replaceTransactionImage(txId, newPath);
        imageStorage.delete(oldPath);
    }
    @Transactional
    /** 修改交易：只允許 amount / note / image */
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

    public TransactionPageView getTransactionPage(UserId userId, YearMonth month) {
        return queryService.getTransactionPage(userId, month);
    }

}
