package com.example.SmartSpent.infrastructure.repository.projection;


import java.time.LocalDateTime;

import com.example.SmartSpent.domain.model.CategoryType;

public interface TransactionRow {

    String getTransactionId(); 
    LocalDateTime getDate();
    CategoryType getCategory();
    int getAmount();
    String getNote();
    String getImagePath();
}
