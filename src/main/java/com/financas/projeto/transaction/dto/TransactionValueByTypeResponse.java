package com.financas.projeto.transaction.dto;

import java.math.BigDecimal;

public record TransactionValueByTypeResponse(
    BigDecimal totalValue
) {
} 
