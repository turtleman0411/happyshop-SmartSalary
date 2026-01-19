package com.example.SmartSpent.application.Transaction;

import java.time.YearMonth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.infrastructure.storage.ImageStorage;
import com.example.SmartSpent.presentation.dto.request.AddTransactionRequest;

@Component

public class TransactionFlow {

    private final AddTransactionService addTransactionService;
    private final DeleteTransactionService deleteTransactionService;
    private final TransactionUpdateService transactionUpdateService;

    // 目前 add 仍需要這兩個（你也可以之後再抽成 service）
    private final BudgetMonthRepository budgetMonthRepository;
    private final ImageStorage imageStorage;

    public TransactionFlow(
            AddTransactionService addTransactionService,
            DeleteTransactionService deleteTransactionService,
            TransactionUpdateService transactionUpdateService,
            BudgetMonthRepository budgetMonthRepository,
            ImageStorage imageStorage
    ) {
        this.addTransactionService = addTransactionService;
        this.deleteTransactionService = deleteTransactionService;
        this.transactionUpdateService = transactionUpdateService;
        this.budgetMonthRepository = budgetMonthRepository;
        this.imageStorage = imageStorage;
    }

    /**
     * 使用者在指定月份新增交易（流程封裝）
     */
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

    // ✅ 新增交易用 request.date() 當檔名日期即可
    String newPath = imageStorage.save(month, txId, request.date(), request.image());

    // ✅ 新 API：替換圖片 + 取得舊路徑
    String oldPath = bm.replaceTransactionImage(txId, newPath);

    // ✅ 刪舊檔（可選）
    imageStorage.delete(oldPath);
}



    /**
     * ✅ 修改交易：只允許 amount / note / image
     */
    public TransactionId updateTransaction(
            UserId userId,
            YearMonth month,
            String rawTransactionId,
            int amount,
            String note,
            MultipartFile image
    ) {
        return transactionUpdateService.update(
                userId, month, rawTransactionId, amount, note, image
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
}
