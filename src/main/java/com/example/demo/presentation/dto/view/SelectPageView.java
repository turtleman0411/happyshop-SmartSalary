package com.example.demo.presentation.dto.view;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public record SelectPageView(
        YearMonth month,
        Map<String, List<CategoryCardView>> groupedCategories,
        String userMode
) {}
