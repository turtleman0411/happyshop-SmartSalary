package com.example.demo.presentation.dto.request;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.domain.model.CategoryType;

public record BudgetAllocationRequest(
        YearMonth month,
        Map<String, Integer> percents
) {

    // ⭐ record compact constructor
    public BudgetAllocationRequest {
        if (percents == null) {
            percents = new HashMap<>();   // ✅ 一定要是 mutable
        }
    }

    public Map<CategoryType, Integer> toCategoryPercentMap() {

        if (percents.isEmpty()) {
            return Map.of();
        }

        return percents.entrySet().stream()
                .filter(e -> e.getKey() != null && !e.getKey().isBlank())
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(
                        e -> CategoryType.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }
}
