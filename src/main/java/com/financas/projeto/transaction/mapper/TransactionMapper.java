package com.financas.projeto.transaction.mapper;

import org.springframework.stereotype.Component;

import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.entity.Transaction;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName());
        return response;
    }

    public Transaction toEntity(RegisterTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        return transaction;
    }
}
