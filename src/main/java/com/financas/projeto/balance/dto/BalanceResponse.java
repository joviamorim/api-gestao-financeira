package com.financas.projeto.balance.dto;

import java.math.BigDecimal;

public record BalanceResponse(
    BigDecimal balance
) {
}
