package com.example.SmartSpent.application.Transaction;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.infrastructure.storage.ImageStorage;

import jakarta.transaction.Transactional;

@Service
 class TransactionUpdateService {

    private final BudgetMonthRepository budgetMonthRepository;
    private final ImageStorage imageStorage;

    public TransactionUpdateService(
            BudgetMonthRepository budgetMonthRepository,
            ImageStorage imageStorage
    ) {
        this.budgetMonthRepository = budgetMonthRepository;
        this.imageStorage = imageStorage;
    }

@Transactional
public TransactionId update(
        UserId userId,
        YearMonth month,
        TransactionId txId,
        int amount,
        String note,
        MultipartFile image
) {
    BudgetMonth bm = budgetMonthRepository
            .findByUserIdAndMonth(userId, month)
            .orElseThrow(() -> new IllegalArgumentException("BudgetMonth not found"));

    // ✅ 只改 amount / note
    bm.updateTransaction(txId, amount, note);

    // ✅ 有上傳才換圖
    if (image != null && !image.isEmpty()) {

        LocalDateTime txDate = bm.getTransactionDate(txId);
        String saved = imageStorage.save(month, txId, txDate, image);
        String oldPath = bm.replaceTransactionImage(txId, saved);
        imageStorage.delete(oldPath);
    }

    return txId;
}

}
