package com.financas.projeto.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financas.projeto.transaction.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterTransactionRequest(
    @NotNull
    TransactionType type,

    @NotNull
    @Positive
    BigDecimal amount,

    @NotBlank
    @Size(max = 255)
    String description,

    @NotNull
    LocalDate date,

    @NotNull
    UUID categoryId
) {
}