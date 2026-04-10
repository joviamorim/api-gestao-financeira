package com.financas.projeto.balance.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.financas.projeto.balance.dto.BalanceResponse;
import com.financas.projeto.balance.exception.BalanceStartDateIsAfterException;
import com.financas.projeto.balance.mapper.BalanceMapper;
import com.financas.projeto.transaction.repository.TransactionRepository;

@Service
public class BalanceService {

    private final TransactionRepository transactionRepository;
    private final BalanceMapper balanceMapper;

    public BalanceService(TransactionRepository transactionRepository, BalanceMapper balanceMapper) {
        this.transactionRepository = transactionRepository;
        this.balanceMapper = balanceMapper;
    }

    public BalanceResponse getBalance(UUID userId) {
        BigDecimal balance = transactionRepository.getBalance(userId);
        return balanceMapper.toResponse(balance);
    }

    public BalanceResponse getBalanceByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BalanceStartDateIsAfterException();
        }

        BigDecimal balance = transactionRepository.getBalanceByDateRange(userId, startDate, endDate);
        return balanceMapper.toResponse(balance);
    }
}
