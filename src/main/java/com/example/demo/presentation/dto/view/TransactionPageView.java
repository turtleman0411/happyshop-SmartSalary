package com.example.demo.presentation.dto.view;

import java.time.YearMonth;
import java.util.List;

public record TransactionPageView(
    YearMonth month,
    List<TransactionItemView> transactionList,
    List<CategoryOptionView> categoryOptions, // 下拉用（含中文）
    String selectedCategory                // RENT / FOOD / ALL / null
) {}
