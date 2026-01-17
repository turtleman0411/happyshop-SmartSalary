package com.example.SmartSpent.infrastructure.repository.projection;

import com.example.SmartSpent.domain.model.CategoryType;

public interface CategoryAllocationRow {

    CategoryType getCategory();

    int getPercent();
}
