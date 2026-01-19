package com.example.SmartSpent.application.Transaction;


import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartSpent.domain.model.BudgetMonth;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.BudgetMonthRepository;
import com.example.SmartSpent.infrastructure.storage.ImageStorage;

@Service
public class TransactionUpdateService {

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
        String rawTransactionId,
        int amount,
        String note,
        MultipartFile image
) {
    BudgetMonth bm = budgetMonthRepository
            .findByUserIdAndMonth(userId, month)
            .orElseThrow(() -> new IllegalArgumentException("BudgetMonth not found"));

    TransactionId txId = TransactionId.of(Long.parseLong(rawTransactionId));

    // ✅ 只改 amount / note
    bm.updateTransaction(txId, amount, note);

    // ✅ 有上傳才換圖
    if (image != null && !image.isEmpty()) {

        // ⭐ 1) 從既有交易取日期（因為你不允許改 date）
        LocalDate txDate = bm.getTransactionDate(txId);

        // ⭐ 2) 存檔回傳相對路徑
        String saved = imageStorage.save(month, txId, txDate, image);

        // ⭐ 3) 寫回交易（建議用 replace 拿舊圖路徑）
        String oldPath = bm.replaceTransactionImage(txId, saved);

        // （可選）刪舊圖檔，避免垃圾檔案
        imageStorage.delete(oldPath);
    }

    return txId;
}

}
