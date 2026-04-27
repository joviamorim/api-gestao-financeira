package com.financas.projeto.integration.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.repository.UserRepository;

public class TestDataFactory {

    public static User createUser(UserRepository repository) {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test-" + UUID.randomUUID() + "@example.com");
        user.setPassword("password");
        return repository.save(user);
    }

    public static Category createCategory(CategoryRepository repository) {
        Category category = new Category();
        category.setName("Test Category");
        return repository.save(category);
    }

    public static Transaction createTransaction(
            User user,
            Category category,
            BigDecimal amount,
            LocalDate date,
            String description,
            TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);
        return transaction;
    }
}
