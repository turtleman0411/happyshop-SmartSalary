package com.example.SmartSpent.application.common;

import java.time.YearMonth;

import org.springframework.stereotype.Component;

@Component
public class MonthThemeResolver {

    public String resolve(YearMonth month) {
        int m = month.getMonthValue();

        if (m == 12) return "theme-winter theme-xmas";
        if (m == 1 || m == 2) return "theme-winter";
        if (m >= 3 && m <= 5) return "theme-spring";
        if (m >= 6 && m <= 8) return "theme-summer";
        return "theme-autumn"; // 9–11
    }
}
