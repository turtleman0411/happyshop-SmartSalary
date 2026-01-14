package com.example.demo.application.query;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.domain.model.CategoryType;
import com.example.demo.domain.value.UserId;
import com.example.demo.infrastructure.repository.CategoryAllocationRepository;
import com.example.demo.infrastructure.repository.TransactionRepository;
import com.example.demo.infrastructure.repository.projection.CategoryAllocationRow;
import com.example.demo.infrastructure.repository.projection.TransactionRow;
import com.example.demo.presentation.dto.view.CategoryOptionView;
import com.example.demo.presentation.dto.view.TransactionItemView;
import com.example.demo.presentation.dto.view.TransactionPageView;

@Service
public class TransactionPageQueryService {

    private final TransactionRepository transactionRepository;
    private final CategoryAllocationRepository allocationRepository;

    public TransactionPageQueryService(
            TransactionRepository transactionRepository,
            CategoryAllocationRepository allocationRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.allocationRepository = allocationRepository;
    }

    public TransactionPageView getTransactionPage(
            UserId userId,
            YearMonth month,
            String selectedCategory // RENT / FOOD / ALL / null
    ) {

        /* =========================
         * 1️⃣ 查「所有交易」（不帶分類）
         * ========================= */
        List<TransactionRow> allTxRows =
                transactionRepository.findTransactions(
                        userId.value(),
                        month,
                        null
                );

        /* =========================
         * 2️⃣ 蒐集「有交易」分類
         * ========================= */
        Set<CategoryType> categoriesFromTransaction =
                allTxRows.stream()
                        .map(TransactionRow::getCategory)
                        .filter(c -> c != null)
                        .collect(Collectors.toSet());

        /* =========================
         * 3️⃣ 蒐集「有分配」分類
         * ========================= */
        List<CategoryAllocationRow> allocationRows =
                allocationRepository.findCategoryAllocations(
                        userId.value(),
                        month
                );

        Set<CategoryType> categoriesFromAllocation =
                allocationRows.stream()
                        .map(CategoryAllocationRow::getCategory)
                        .filter(c -> c != null)
                        .collect(Collectors.toSet());

        /* =========================
         * 4️⃣ 合併可用分類（交易 ∪ 分配）
         * ========================= */
        Set<CategoryType> availableCategories = new HashSet<>();
        availableCategories.addAll(categoriesFromTransaction);
        availableCategories.addAll(categoriesFromAllocation);

        /* =========================
         * 5️⃣ 防呆 selectedCategory（前端用 String）
         * ========================= */
        String safeCategory =
                normalizeCategory(selectedCategory, availableCategories);

        /* =========================
         * 6️⃣ String → Enum（只在後端做）
         * ========================= */
        CategoryType categoryType = null;
        if (safeCategory != null) {
            categoryType = CategoryType.valueOf(safeCategory);
        }

        /* =========================
         * 7️⃣ 依安全分類重新查交易（Enum）
         * ========================= */
        List<TransactionRow> txRows =
                transactionRepository.findTransactions(
                        userId.value(),
                        month,
                        categoryType
                );

        List<TransactionItemView> transactionList =
                txRows.stream()
                        .map(r -> new TransactionItemView(
                                r.getTransactionId(),
                                r.getDate(),
                                r.getCategory().name(),
                                r.getCategory().displayName(),
                                r.getAmount(),
                                r.getNote(),
                                r.getImagePath() == null
                                ? null
                                : "/uploads/" + r.getImagePath()
                        ))
                        .toList();

        /* =========================
         * 8️⃣ 組下拉選單
         * ========================= */
        List<CategoryOptionView> categoryOptions =
                availableCategories.stream()
                        .sorted(Enum::compareTo)
                        .map(c -> new CategoryOptionView(
                                c.name(),
                                c.displayName()
                        ))
                        .toList();

        return new TransactionPageView(
                month,
                transactionList,
                categoryOptions,
                safeCategory
        );
    }

    /**
     * 前端安全分類檢查（只回傳 String 給 View 用）
     */
    private String normalizeCategory(
            String raw,
            Set<CategoryType> availableCategories
    ) {

        if (raw == null || raw.isBlank() || "ALL".equals(raw)) {
            return null;
        }

        try {
            CategoryType type = CategoryType.valueOf(raw);
            return availableCategories.contains(type) ? raw : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
