package com.example.demo.application.result;

import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.demo.application.query.ResultPageQueryService;
import com.example.demo.domain.value.UserId;
import com.example.demo.presentation.dto.view.ResultPageView;

@Service
public class ResultPageFlow {

    private final ResultPageQueryService queryService;

    public ResultPageFlow(ResultPageQueryService queryService) {
        this.queryService = queryService;
    }

    public ResultPageFlowResult getResultPage(UserId userId, YearMonth month) {

        ResultPageView view =
                queryService.getResultPage(userId, month);

        String themeClass = resolveMonthTheme(month);

        return new ResultPageFlowResult(view, themeClass);
    }

   private String resolveMonthTheme(YearMonth month) {
    int m = month.getMonthValue();

    if (m == 12) return "theme-winter theme-xmas";
    if (m == 1 || m == 2) return "theme-winter";
    if (m >= 3 && m <= 5) return "theme-spring";
    if (m >= 6 && m <= 8) return "theme-summer";
    return "theme-autumn"; // 9–11
}

}
