package com.example.SmartSpent.presentation.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public record AddTransactionRequest(
    String categoryName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date,
    int amount,
    String note,
    MultipartFile image
) {}
