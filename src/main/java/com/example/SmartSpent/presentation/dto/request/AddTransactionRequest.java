package com.example.SmartSpent.presentation.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public record AddTransactionRequest(
    String categoryName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime date,
    int amount,
    String note,
    MultipartFile image
) {}
