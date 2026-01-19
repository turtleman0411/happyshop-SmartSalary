package com.example.SmartSpent.presentation.dto.request;

import org.springframework.web.multipart.MultipartFile;
import com.example.SmartSpent.domain.value.TransactionId;

public record UpdateTransactionRequest(
        TransactionId transactionId,
        int amount,
        String note,
        MultipartFile image
) {}
