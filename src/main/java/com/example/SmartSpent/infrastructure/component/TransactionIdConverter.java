package com.example.SmartSpent.infrastructure.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.SmartSpent.domain.value.TransactionId;

@Component
public class TransactionIdConverter implements Converter<String, TransactionId> {
    @Override
    public TransactionId convert(String source) {
        if (source == null || source.isBlank()) return null;
        return TransactionId.of(source.trim());
    }
}
