package com.financas.projeto.transaction;

import java.util.List;
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

    public List<Transaction> getAllTransactionsByUserEmail(String userEmail) {
        List<Transaction> transactions = transactionRepository.findAllByUserEmail(userEmail);
        return transactions;
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
