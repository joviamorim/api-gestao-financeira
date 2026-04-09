package com.financas.projeto.transaction.exception;

public class TransactionUnauthorizedException extends RuntimeException {
    public TransactionUnauthorizedException() {
        super("You are not authorized to access this transaction.");
    }

}
