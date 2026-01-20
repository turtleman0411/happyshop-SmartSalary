package com.example.SmartSpent.application.query;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.CategoryAllocationRepository;
import com.example.SmartSpent.infrastructure.repository.TransactionRepository;
import com.example.SmartSpent.infrastructure.repository.projection.CategoryAllocationRow;
import com.example.SmartSpent.infrastructure.repository.projection.TransactionRow;
import com.example.SmartSpent.presentation.dto.view.CategoryOptionView;
import com.example.SmartSpent.presentation.dto.view.TransactionDateGroupView;
import com.example.SmartSpent.presentation.dto.view.TransactionItemView;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

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

    /**
     * ✅ 單一入口：永遠查「本月全部交易」
     * - 下拉選單：前端即時過濾（不導頁、不查 DB）
     * - 最近 3 筆：完全不受分類影響
     * - 主清單：依日期分組（LocalDate）
     */
    public TransactionPageView getTransactionPage(UserId userId, YearMonth month) {

        /* =========================
         * 1️⃣ 查「本月全部交易」
         * ========================= */
        List<TransactionRow> allTxRows = transactionRepository.findAllTransactions(
                userId.value(),
                month
        );

        /* =========================
         * 2️⃣ 最近 3 筆（本月，不受分類影響）
         * ========================= */
        List<TransactionItemView> recentTransactions = transactionRepository
                .findRecentTransactions(userId.value(), month, PageRequest.of(0, 3))
                .stream()
                .map(this::toItemView)
                .toList();

        /* =========================
         * 3️⃣ 下拉選單可用分類（交易 ∪ 分配）
         * ========================= */
        Set<CategoryType> categoriesFromTx = allTxRows.stream()
                .map(TransactionRow::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CategoryAllocationRow> allocationRows = allocationRepository.findCategoryAllocations(
                userId.value(),
                month
        );

        Set<CategoryType> categoriesFromAllocation = allocationRows.stream()
                .map(CategoryAllocationRow::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<CategoryType> availableCategories = new HashSet<>();
        availableCategories.addAll(categoriesFromTx);
        availableCategories.addAll(categoriesFromAllocation);

        List<CategoryOptionView> categoryOptions = availableCategories.stream()
                .sorted(Enum::compareTo)
                .map(c -> new CategoryOptionView(c.name(), c.displayName()))
                .toList();

        /* =========================
         * 4️⃣ 主清單：依日期分組（LocalDate）
         * - 因為你現在是 LocalDateTime，所以分組要用 toLocalDate()
         * ========================= */
        List<TransactionItemView> allItems = allTxRows.stream()
                .map(this::toItemView)
                .toList();

        Map<LocalDate, List<TransactionItemView>> grouped = allItems.stream()
                .collect(Collectors.groupingBy(
                        it -> it.date().toLocalDate(),
                        TreeMap::new, // 舊→新
                        Collectors.toList()
                ));

        NavigableMap<LocalDate, List<TransactionItemView>> desc =
                new TreeMap<>(Comparator.reverseOrder());
        desc.putAll(grouped);

        List<TransactionDateGroupView> groupedTransactions = desc.entrySet().stream()
                .map(e -> {
                    LocalDate date = e.getKey();
                    List<TransactionItemView> list = e.getValue();

                    int dayTotal = list.stream()
                            .mapToInt(TransactionItemView::amount)
                            .sum();

                    return TransactionDateGroupView.of(date, dayTotal, list);
                })
                .toList();

        /* =========================
         * 5️⃣ 組 View（不再有 selectedCategory）
         * ========================= */
        return TransactionPageView.of(
                month,
                groupedTransactions,
                recentTransactions,
                categoryOptions
        );
    }

    private TransactionItemView toItemView(TransactionRow r) {
        return new TransactionItemView(
                r.getTransactionId(),
                r.getDate(),
                r.getCategory().name(),
                r.getCategory().displayName(),
                r.getAmount(),
                r.getNote(),
                r.getImagePath() == null ? null : "/uploads/" + r.getImagePath()
        );
    }
}
