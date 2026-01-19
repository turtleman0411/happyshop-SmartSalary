package com.example.SmartSpent.infrastructure.repository.projection;

import java.time.LocalDate;

import com.example.SmartSpent.domain.model.CategoryType;

public interface TransactionRow {

    String getTransactionId(); 
    LocalDate getDate();
    CategoryType getCategory();
    int getAmount();
    String getNote();
    String getImagePath();
}
