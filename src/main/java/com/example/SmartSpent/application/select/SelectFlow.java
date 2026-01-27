package com.example.SmartSpent.application.select;

import java.time.YearMonth;

import com.example.SmartSpent.presentation.dto.view.SelectPageView;

public class SelectFlow {
    private final SelectPageQueryService queryService;
    public SelectFlow(SelectPageQueryService queryService){
        this.queryService = queryService;
    }

    public SelectPageView getPageView(Object userId,YearMonth month){
        return queryService.getPageView(userId, month);
    }
}
