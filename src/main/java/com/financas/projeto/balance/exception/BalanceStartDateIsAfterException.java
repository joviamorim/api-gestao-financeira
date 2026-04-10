package com.financas.projeto.balance.exception;

public class BalanceStartDateIsAfterException extends RuntimeException {
    public BalanceStartDateIsAfterException() {
        super("Invalid date range: startDate must be before endDate");
    }
}
