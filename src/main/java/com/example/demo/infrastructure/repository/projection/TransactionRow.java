package com.example.demo.infrastructure.repository.projection;

import java.time.LocalDate;

import com.example.demo.domain.model.CategoryType;

public interface TransactionRow {

    Long getTransactionId();
    LocalDate getDate();
    CategoryType getCategory();
    int getAmount();
    String getNote();
    String getImagePath();
}
