package com.example.SmartSpent.presentation.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateTransactionRequest(
        String transactionId,
        int amount,
        String note,
        MultipartFile image
) {}
