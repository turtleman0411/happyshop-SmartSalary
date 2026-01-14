package com.example.demo.infrastructure.repository.projection;

import com.example.demo.domain.model.CategoryType;

public interface CategorySpentRow {

    CategoryType getCategory();

    int getSpentAmount();
}
