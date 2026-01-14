package com.example.demo.infrastructure.repository.projection;

import com.example.demo.domain.model.CategoryType;

public interface CategoryAllocationRow {

    CategoryType getCategory();

    int getPercent();
}
