package com.financas.projeto.transaction.exception;

public class TransactionStartDateIsAfterException extends RuntimeException {
    public TransactionStartDateIsAfterException() {
        super("Invalid date range: startDate must be before endDate");
    }

}
