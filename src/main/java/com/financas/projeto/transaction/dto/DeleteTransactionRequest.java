package com.financas.projeto.transaction.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record DeleteTransactionRequest(
    @NotNull
    UUID id
) {
}
