package com.example.SmartSpent.presentation.dto.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

public record AddTransactionRequest(
    String categoryName,
    LocalDate date,
    int amount,
    String note,
    MultipartFile image
) {}
