package com.financas.projeto.balance.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.financas.projeto.transaction.service.TransactionService;

@Service
public class BalanceService {

    private final TransactionService transactionService;

    public BalanceService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public BigDecimal getBalance(UUID userId) {
        return transactionService.getBalance(userId);
    }

    public BigDecimal getBalanceByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return transactionService.getBalanceByDateRange(userId, startDate, endDate);
    }
}
