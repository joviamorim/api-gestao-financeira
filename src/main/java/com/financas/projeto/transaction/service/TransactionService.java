package com.financas.projeto.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.dto.TransactionValueByTypeResponse;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.transaction.exception.TransactionNotFoundException;
import com.financas.projeto.transaction.exception.TransactionStartDateIsAfterException;
import com.financas.projeto.transaction.exception.TransactionUnauthorizedException;
import com.financas.projeto.transaction.mapper.TransactionMapper;
import com.financas.projeto.transaction.repository.TransactionRepository;
import com.financas.projeto.user.entity.User;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.categoryRepository = categoryRepository;
    }

    public Page<TransactionResponse> getAllTransactionsByUserId(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAllByUserId(
                userId,
                pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    public Page<TransactionResponse> getTransactionsByUserIdAndDateBetween(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new TransactionStartDateIsAfterException();
        }

        Page<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId,
                startDate,
                endDate,
                pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    public Page<TransactionResponse> getTransactionsByUserIdAndCategory(
            UUID userId,
            UUID categoryId,
            Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }

        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                userId,
                categoryId,
                pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    public Page<TransactionResponse> getTransactionsByUserIdAndType(
            UUID userId,
            TransactionType type,
            Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndType(
                userId,
                type,
                pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    public TransactionValueByTypeResponse getTotalValueByUserIdAndType(UUID userId, TransactionType type) {
        BigDecimal totalValue = transactionRepository.getTotalValueByUserIdAndType(userId, type);
        return new TransactionValueByTypeResponse(totalValue);
    }

    public TransactionResponse createTransaction(RegisterTransactionRequest request, User user) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException());

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);
        transaction.setCategory(category);

        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    public TransactionResponse updateTransaction(UpdateTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdWithCategory(request.id())
                .orElseThrow(() -> new TransactionNotFoundException());

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new TransactionUnauthorizedException();
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException());

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setCategory(category);

        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    public TransactionResponse deleteTransaction(DeleteTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdWithCategory(request.id())
                .orElseThrow(() -> new TransactionNotFoundException());

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new TransactionUnauthorizedException();
        }

        transactionRepository.delete(transaction);
        return transactionMapper.toResponse(transaction);
    }

}
