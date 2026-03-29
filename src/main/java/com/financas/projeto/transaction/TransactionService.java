package com.financas.projeto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.financas.projeto.category.Category;
import com.financas.projeto.category.CategoryService;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.user.User;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public Page<Transaction> getAllTransactionsByUserId(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAllByUserId(
            userId,
            pageable
        );

        return transactions;
    }

    public Page<Transaction> getTransactionsByUserIdAndDateBetween(
        UUID userId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
            userId,
            startDate,
            endDate,
            pageable
        );

        return transactions;
    }

    public Page<Transaction> getTransactionsByUserIdAndCategory(
        UUID userId,
        UUID categoryId,
        Pageable pageable
    ) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
            userId,
            categoryId,
            pageable
        );

        return transactions;
    }

    public Page<Transaction> getTransactionsByUserIdAndType(
        UUID userId,
        TransactionType type,
        Pageable pageable
    ) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndType(
            userId,
            type,
            pageable
        );

        return transactions;
    }
    
    public BigDecimal getTotalValueByUserIdAndType(UUID userId, TransactionType type) {
        return transactionRepository.getTotalValueByUserIdAndType(userId, type);
    }

    public BigDecimal getBalance(UUID userId) {
        return transactionRepository.getBalance(userId);
    }

    public BigDecimal getBalanceByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getBalanceByDateRange(userId, startDate, endDate);
    }

    public Transaction createTransaction(RegisterTransactionRequest request, User user) {
        Category category = categoryService.findCategoryById(request.categoryId());

        Transaction transaction = new Transaction();
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setUser(user);
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(UpdateTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findById(request.id())
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Category category = categoryService.findCategoryById(request.categoryId());

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    public Transaction deleteTransaction(DeleteTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findById(request.id())
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        transactionRepository.delete(transaction);
        
        return transaction;
    }

}
