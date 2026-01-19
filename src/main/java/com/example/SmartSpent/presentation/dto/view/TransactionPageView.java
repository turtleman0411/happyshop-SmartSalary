package com.example.SmartSpent.presentation.dto.view;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public record TransactionPageView(
    YearMonth month,

    /** ✅ 本頁主清單（可能依分類） */
    List<TransactionItemView> transactionList,

    /** ✅ ⭐ 新增：最近 3 筆（永遠不篩選） */
    List<TransactionItemView> recentTransactions,

    List<CategoryOptionView> categoryOptions,
    String selectedCategory,
    List<TransactionDateGroupView> groupedTransactions
) {

    /** ✅ 唯一對外入口（Controller / Query 只能用這個） */
    public static TransactionPageView of(
            YearMonth month,
            List<TransactionItemView> transactionList,
            List<TransactionItemView> recentTransactions,
            List<CategoryOptionView> categoryOptions,
            String selectedCategory
    ) {

        List<TransactionDateGroupView> grouped =
                transactionList.stream()
                        .collect(Collectors.groupingBy(
                                TransactionItemView::date,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ))
                        .entrySet()
                        .stream()
                        .map(e -> new TransactionDateGroupView(
                                e.getKey(),
                                e.getValue(),
                                e.getValue()
                                        .stream()
                                        .mapToInt(TransactionItemView::amount)
                                        .sum()
                        ))
                        .toList();

        return new TransactionPageView(
                month,
                transactionList,
                recentTransactions,
                categoryOptions,
                selectedCategory,
                grouped
        );
    }
}
