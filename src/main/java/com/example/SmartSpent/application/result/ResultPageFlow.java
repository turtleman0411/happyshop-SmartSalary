package com.example.SmartSpent.application.result;

import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.query.ResultPageQueryService;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.view.ResultPageView;

@Service
public class ResultPageFlow {

    private final ResultPageQueryService queryService;
    
    public ResultPageFlow(ResultPageQueryService queryService) {
        this.queryService = queryService;
    }

    public  ResultPageView getResultPage(UserId userId, YearMonth month) {

        ResultPageView view =
                queryService.getResultPage(userId, month);

        return view;
    }


}
