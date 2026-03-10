package com.financas.projeto.transaction.dto;

import java.util.UUID;

public record DeleteTransactionRequest(
    UUID id
) {
}
