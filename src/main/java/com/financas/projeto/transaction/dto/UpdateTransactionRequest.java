package com.financas.projeto.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financas.projeto.transaction.TransactionType;

public record UpdateTransactionRequest(
    UUID id,
    TransactionType type,
    BigDecimal amount,
    String description,
    LocalDate date,
    UUID categoryId
) {
}
