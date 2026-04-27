package com.financas.projeto.unit.balance.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financas.projeto.balance.dto.BalanceResponse;
import com.financas.projeto.balance.exception.BalanceStartDateIsAfterException;
import com.financas.projeto.balance.mapper.BalanceMapper;
import com.financas.projeto.balance.service.BalanceService;
import com.financas.projeto.transaction.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private BalanceMapper balanceMapper;

        @InjectMocks
        private BalanceService balanceService;

        @Test
        void shouldGetBalanceSuccessfully() {
                UUID userId = UUID.randomUUID();
                BigDecimal balance = new BigDecimal(9.99);

                BalanceResponse response = new BalanceResponse(balance);

                when(transactionRepository.getBalance(userId))
                                .thenReturn(balance);

                when(balanceMapper.toResponse(balance))
                                .thenReturn(response);

                BalanceResponse result = balanceService.getBalance(userId);

                assertSame(response, result);
                verify(transactionRepository).getBalance(userId);
                verify(balanceMapper).toResponse(balance);
        }

        @Test
        void shouldGetBalanceByDateRangeSuccessfully() {
                UUID userId = UUID.randomUUID();
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);
                BigDecimal balance = new BigDecimal(9.99);

                BalanceResponse response = new BalanceResponse(balance);

                when(transactionRepository.getBalanceByDateRange(userId, startDate, endDate))
                                .thenReturn(balance);

                when(balanceMapper.toResponse(balance))
                                .thenReturn(response);

                BalanceResponse result = balanceService.getBalanceByDateRange(userId, startDate, endDate);

                assertSame(response, result);
                verify(transactionRepository).getBalanceByDateRange(userId, startDate, endDate);
                verify(balanceMapper).toResponse(balance);
        }

        @Test
        void shouldThrowWhenStartDateIsAfterEndDate() {
                UUID userId = UUID.randomUUID();
                LocalDate startDate = LocalDate.of(2026, 1, 31);
                LocalDate endDate = LocalDate.of(2026, 1, 1);

                assertThrows(BalanceStartDateIsAfterException.class,
                                () -> balanceService.getBalanceByDateRange(userId, startDate, endDate));

                verifyNoInteractions(transactionRepository);
                verifyNoInteractions(balanceMapper);
        }
}
