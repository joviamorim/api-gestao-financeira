package com.financas.projeto.balance.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.financas.projeto.balance.dto.BalanceResponse;

@Component
public class BalanceMapper {

    public BalanceResponse toResponse(BigDecimal balance) {
        return new BalanceResponse(balance);
    }
}
