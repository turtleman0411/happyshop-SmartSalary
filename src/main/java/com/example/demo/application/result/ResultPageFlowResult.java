package com.example.demo.application.result;

import com.example.demo.presentation.dto.view.ResultPageView;

public record ResultPageFlowResult(
        ResultPageView view,
        String themeClass
) {}
