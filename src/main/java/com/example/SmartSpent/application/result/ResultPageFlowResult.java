package com.example.SmartSpent.application.result;

import com.example.SmartSpent.presentation.dto.view.ResultPageView;

public record ResultPageFlowResult(
        ResultPageView view,
        String themeClass
) {}
