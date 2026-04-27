package com.financas.projeto.integration.transaction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.integration.config.IntegrationTestBase;
import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.transaction.repository.TransactionRepository;
import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.repository.UserRepository;

public class TransactionRepositoryIT extends IntegrationTestBase {

        @Autowired
        private TransactionRepository transactionRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        private User createUser() {
                User user = new User();
                user.setName("Test User");
                user.setEmail("test" + UUID.randomUUID().toString() + "@example.com");
                user.setPassword("password");
                return userRepository.save(user);
        }

        private Category createCategory() {
                Category category = new Category();
                category.setName("Test Category");
                return categoryRepository.save(category);
        }

        private Transaction createTransaction(User user, Category category, BigDecimal amount,
                        TransactionType type, LocalDate date) {
                Transaction transaction = new Transaction();
                transaction.setUser(user);
                transaction.setCategory(category);
                transaction.setAmount(amount);
                transaction.setDescription("Test Transaction");
                transaction.setDate(date);
                transaction.setType(type);
                return transactionRepository.save(transaction);
        }

        @Test
        void shouldSaveTransaction() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction = createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.of(2024, 1, 1));

                Transaction saved = transactionRepository.save(transaction);

                assertNotNull(saved.getId());
                assertEquals(user.getId(), saved.getUser().getId());
                assertEquals(category.getId(), saved.getCategory().getId());
        }

        @Test
        void shouldReturnEmptyWhenUserHasNoTransactions() {
                User user = createUser();

                Pageable pageable = Pageable.unpaged();

                Page<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable);

                assertNotNull(transactions);
                assertTrue(transactions.isEmpty());
        }

        @Test
        void shouldReturnTransactionsWhenUserHasTransactions() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction = createTransaction(user, category, new BigDecimal("50"),
                                TransactionType.EXPENSE,
                                LocalDate.now());

                transactionRepository.save(transaction);

                Page<Transaction> result = transactionRepository.findAllByUserId(user.getId(), Pageable.unpaged());

                assertFalse(result.isEmpty());
                assertEquals(1, result.getTotalElements());
        }

        @Test
        void shouldFindByUserAndDateRange() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction1 = createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.of(2024, 1, 10));

                Transaction transaction2 = createTransaction(user, category, new BigDecimal("50"),
                                TransactionType.EXPENSE, LocalDate.of(2024, 1, 20));

                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);

                Page<Transaction> result = transactionRepository.findByUserIdAndDateBetween(
                                user.getId(),
                                LocalDate.of(2024, 1, 10),
                                LocalDate.of(2024, 1, 25),
                                Pageable.unpaged());

                assertFalse(result.isEmpty());
                assertEquals(2, result.getTotalElements());
        }

        @Test
        void shouldGetBalanceByUserId() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction1 = createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.of(2024, 1, 1));

                Transaction transaction2 = createTransaction(user, category, new BigDecimal("50"),
                                TransactionType.EXPENSE, LocalDate.of(2024, 1, 20));

                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);

                BigDecimal balance = transactionRepository.getBalance(user.getId());

                assertNotNull(balance);
                assertEquals(0, balance.compareTo(new BigDecimal("50")));
        }

        @Test
        void shouldGetBalanceByUserIdAndDateRange() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction1 = createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.of(2024, 1, 15));

                Transaction transaction2 = createTransaction(user, category, new BigDecimal("50"),
                                TransactionType.EXPENSE, LocalDate.of(2024, 1, 20));

                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);

                BigDecimal balance = transactionRepository.getBalanceByDateRange(
                                user.getId(),
                                LocalDate.of(2024, 1, 10),
                                LocalDate.of(2024, 1, 25));

                assertNotNull(balance);
                assertEquals(0, balance.compareTo(new BigDecimal("50")));
        }

        @Test
        void shouldGetTotalValueByUserIdAndType() {
                User user = createUser();
                Category category = createCategory();

                Transaction transaction1 = createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.now());

                Transaction transaction2 = createTransaction(user, category, new BigDecimal("50"),
                                TransactionType.EXPENSE, LocalDate.now());

                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);

                BigDecimal totalIncome = transactionRepository.getTotalValueByUserIdAndType(
                                user.getId(),
                                TransactionType.INCOME);

                BigDecimal totalExpense = transactionRepository.getTotalValueByUserIdAndType(
                                user.getId(),
                                TransactionType.EXPENSE);

                assertNotNull(totalIncome);
                assertNotNull(totalExpense);
                assertEquals(0, totalIncome.compareTo(new BigDecimal("100")));
                assertEquals(0, totalExpense.compareTo(new BigDecimal("50")));
        }

        @Test
        void shouldNotReturnTransactionsFromAnotherUser() {
                User user1 = createUser();
                User user2 = createUser();
                Category category = createCategory();

                createTransaction(user1, category, new BigDecimal("100"), TransactionType.INCOME, LocalDate.now());

                Page<Transaction> result = transactionRepository.findAllByUserId(
                                user2.getId(), Pageable.unpaged());

                assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnZeroBalanceWhenUserHasNoTransactions() {
                User user = createUser();

                BigDecimal balance = transactionRepository.getBalance(user.getId());

                assertNotNull(balance);
                assertEquals(0, balance.compareTo(BigDecimal.ZERO));
        }

        @Test
        void shouldReturnEmptyWhenNoTransactionsInDateRange() {
                User user = createUser();
                Category category = createCategory();

                createTransaction(user, category, new BigDecimal("100"),
                                TransactionType.INCOME, LocalDate.of(2024, 1, 1));

                Page<Transaction> result = transactionRepository.findByUserIdAndDateBetween(
                                user.getId(),
                                LocalDate.of(2024, 2, 1),
                                LocalDate.of(2024, 2, 28),
                                Pageable.unpaged());

                assertTrue(result.isEmpty());
        }
}
