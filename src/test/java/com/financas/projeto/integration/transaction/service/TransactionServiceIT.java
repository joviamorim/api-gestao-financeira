package com.financas.projeto.integration.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.integration.config.IntegrationTestBase;
import com.financas.projeto.integration.util.TestDataFactory;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.transaction.exception.TransactionNotFoundException;
import com.financas.projeto.transaction.exception.TransactionStartDateIsAfterException;
import com.financas.projeto.transaction.repository.TransactionRepository;
import com.financas.projeto.transaction.service.TransactionService;
import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.repository.UserRepository;

public class TransactionServiceIT extends IntegrationTestBase {

        @Autowired
        TransactionService transactionService;

        @Autowired
        TransactionRepository transactionRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        CategoryRepository categoryRepository;

        @Test
        void shouldGetAllTransactionsByUserIdSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction 1",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "Test Transaction 2",
                                TransactionType.EXPENSE);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getAllTransactionsByUserId(user.getId(),
                                pageable);

                assertNotNull(response);
                assertEquals(2, response.getContent().size());

                List<TransactionResponse> transactions = response.getContent();

                assertTrue(transactions.stream()
                                .anyMatch(t -> t.description().equals("Test Transaction 1")
                                                && t.amount().compareTo(BigDecimal.valueOf(100)) == 0
                                                && t.type() == TransactionType.INCOME
                                                && t.categoryName().equals(category.getName())));

                assertTrue(transactions.stream()
                                .anyMatch(t -> t.description().equals("Test Transaction 2")
                                                && t.amount().compareTo(BigDecimal.valueOf(200)) == 0
                                                && t.type() == TransactionType.EXPENSE
                                                && t.categoryName().equals(category.getName())));
        }

        @Test
        void shouldNotReturnTransactionsFromOtherUsers() {
                User user1 = TestDataFactory.createUser(userRepository);
                User user2 = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user1,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "User 1 Transaction",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user2,
                                category,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "User 2 Transaction",
                                TransactionType.EXPENSE);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getAllTransactionsByUserId(user1.getId(),
                                pageable);

                assertNotNull(response);
                assertEquals(1, response.getContent().size());

                List<TransactionResponse> transactionResponses = response.getContent();

                assertTrue(transactionResponses.stream()
                                .anyMatch(t -> t.description().equals("User 1 Transaction")
                                                && t.amount().compareTo(BigDecimal.valueOf(100)) == 0
                                                && t.type() == TransactionType.INCOME
                                                && t.categoryName().equals(category.getName())));
        }

        @Test
        void shouldReturnEmptyPageWhenUserHasNoTransactions() {
                User user = TestDataFactory.createUser(userRepository);

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getAllTransactionsByUserId(user.getId(),
                                pageable);

                assertNotNull(response);
                assertTrue(response.getContent().isEmpty());
        }

        @Test
        void shouldGetTransactionsByUserIdAndDateBetweenSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction 1",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "Test Transaction 2",
                                TransactionType.EXPENSE);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                LocalDate startDate = LocalDate.of(2026, 4, 9);
                LocalDate endDate = LocalDate.of(2026, 4, 11);
                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndDateBetween(
                                user.getId(),
                                startDate,
                                endDate,
                                pageable);

                assertNotNull(response);
                assertEquals(1, response.getContent().size());

                List<TransactionResponse> transactionResponses = response.getContent();

                assertTrue(transactionResponses.stream()
                                .anyMatch(t -> t.description().equals("Test Transaction 1")
                                                && t.amount().compareTo(BigDecimal.valueOf(100)) == 0
                                                && t.type() == TransactionType.INCOME
                                                && t.categoryName().equals(category.getName())));
        }

        @Test
        void shouldReturnEmptyPageWhenNoTransactionsInDateRange() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                LocalDate startDate = LocalDate.of(2026, 4, 11);
                LocalDate endDate = LocalDate.of(2026, 4, 12);
                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndDateBetween(
                                user.getId(),
                                startDate,
                                endDate,
                                pageable);

                assertNotNull(response);
                assertTrue(response.getContent().isEmpty());
        }

        @Test
        void shouldThrowWhenStartDateIsAfterEndDate() {
                User user = TestDataFactory.createUser(userRepository);

                LocalDate startDate = LocalDate.of(2026, 4, 15);
                LocalDate endDate = LocalDate.of(2026, 4, 10);
                Pageable pageable = PageRequest.of(0, 10);

                assertThrows(TransactionStartDateIsAfterException.class, () -> {
                        transactionService.getTransactionsByUserIdAndDateBetween(
                                        user.getId(),
                                        startDate,
                                        endDate,
                                        pageable);
                });
        }

        @Test
        void shouldGetTransactionsByUserIdAndCategorySuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category1 = TestDataFactory.createCategory(categoryRepository);
                Category category2 = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user,
                                category1,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction 1",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user,
                                category2,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "Test Transaction 2",
                                TransactionType.EXPENSE);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndCategory(
                                user.getId(),
                                category1.getId(),
                                pageable);

                assertNotNull(response);
                assertEquals(1, response.getContent().size());

                List<TransactionResponse> transactionResponses = response.getContent();

                assertTrue(transactionResponses.stream()
                                .anyMatch(t -> t.description().equals("Test Transaction 1")
                                                && t.amount().compareTo(BigDecimal.valueOf(100)) == 0
                                                && t.type() == TransactionType.INCOME
                                                && t.categoryName().equals(category1.getName())));
        }

        @Test
        void shouldReturnEmptyPageWhenNoTransactionsForCategory() {
                User user = TestDataFactory.createUser(userRepository);
                Category category1 = TestDataFactory.createCategory(categoryRepository);
                Category category2 = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category1,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndCategory(
                                user.getId(),
                                category2.getId(),
                                pageable);

                assertNotNull(response);
                assertTrue(response.getContent().isEmpty());
        }

        @Test
        void shouldThrowWhenCategoryNotFound() {
                User user = TestDataFactory.createUser(userRepository);
                UUID nonExistentCategoryId = UUID.randomUUID();

                Pageable pageable = PageRequest.of(0, 10);

                assertThrows(CategoryNotFoundException.class, () -> {
                        transactionService.getTransactionsByUserIdAndCategory(
                                        user.getId(),
                                        nonExistentCategoryId,
                                        pageable);
                });
        }

        @Test
        void shouldGetTransactionsByUserIdAndTypeSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction 1",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "Test Transaction 2",
                                TransactionType.EXPENSE);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndType(
                                user.getId(),
                                TransactionType.INCOME,
                                pageable);

                assertNotNull(response);
                assertEquals(1, response.getContent().size());

                List<TransactionResponse> transactionResponses = response.getContent();

                assertTrue(transactionResponses.stream()
                                .anyMatch(t -> t.description().equals("Test Transaction 1")
                                                && t.amount().compareTo(BigDecimal.valueOf(100)) == 0
                                                && t.type() == TransactionType.INCOME
                                                && t.categoryName().equals(category.getName())));
        }

        @Test
        void shouldReturnEmptyPageWhenNoTransactionsForType() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                Pageable pageable = PageRequest.of(0, 10);

                Page<TransactionResponse> response = transactionService.getTransactionsByUserIdAndType(
                                user.getId(),
                                TransactionType.EXPENSE,
                                pageable);

                assertNotNull(response);
                assertTrue(response.getContent().isEmpty());
        }

        @Test
        void shouldGetTotalValueByUserIdAndTypeSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction1 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction 1",
                                TransactionType.INCOME);

                Transaction transaction2 = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(200),
                                LocalDate.of(2026, 4, 12),
                                "Test Transaction 2",
                                TransactionType.INCOME);

                transactionRepository.saveAll(List.of(transaction1, transaction2));

                BigDecimal totalValue = transactionService.getTotalValueByUserIdAndType(user.getId(),
                                TransactionType.INCOME).totalValue();

                assertNotNull(totalValue);
                assertTrue(totalValue.compareTo(BigDecimal.valueOf(300)) == 0);
        }

        @Test
        void shouldReturnZeroWhenNoTransactionsForType() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                BigDecimal totalValue = transactionService.getTotalValueByUserIdAndType(user.getId(),
                                TransactionType.EXPENSE).totalValue();

                assertNotNull(totalValue);
                assertTrue(totalValue.compareTo(BigDecimal.ZERO) == 0);
        }

        @Test
        void shouldCreateTransactionSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                RegisterTransactionRequest request = new RegisterTransactionRequest(
                                TransactionType.INCOME,
                                BigDecimal.valueOf(150),
                                "New Transaction",
                                LocalDate.of(2026, 4, 15),
                                category.getId());

                TransactionResponse response = transactionService.createTransaction(request, user);

                assertNotNull(response);
                assertEquals("New Transaction", response.description());
                assertEquals(BigDecimal.valueOf(150), response.amount());
                assertEquals(TransactionType.INCOME, response.type());
                assertEquals(category.getName(), response.categoryName());
        }

        @Test
        void shouldThrowWhenCreatingTransactionWithNonExistentCategory() {
                User user = TestDataFactory.createUser(userRepository);
                UUID nonExistentCategoryId = UUID.randomUUID();

                RegisterTransactionRequest request = new RegisterTransactionRequest(
                                TransactionType.INCOME,
                                BigDecimal.valueOf(150),
                                "New Transaction",
                                LocalDate.of(2026, 4, 15),
                                nonExistentCategoryId);

                assertThrows(CategoryNotFoundException.class, () -> {
                        transactionService.createTransaction(request, user);
                });
        }

        @Test
        void shouldUpdateTransactionSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category1 = TestDataFactory.createCategory(categoryRepository);
                Category category2 = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category1,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                UpdateTransactionRequest createRequest = new UpdateTransactionRequest(
                                transaction.getId(),
                                TransactionType.EXPENSE,
                                BigDecimal.valueOf(200),
                                "Updated Transaction",
                                LocalDate.of(2026, 4, 12),
                                category2.getId());

                TransactionResponse response = transactionService.updateTransaction(createRequest, user);

                assertNotNull(response);
                assertEquals("Updated Transaction", response.description());
                assertTrue(response.amount().compareTo(BigDecimal.valueOf(200)) == 0);
                assertEquals(TransactionType.EXPENSE, response.type());
                assertEquals(category2.getName(), response.categoryName());
        }

        @Test
        void shouldThrowWhenUpdatingNonExistentTransaction() {
                User user = TestDataFactory.createUser(userRepository);
                UUID nonExistentTransactionId = UUID.randomUUID();
                Category category = TestDataFactory.createCategory(categoryRepository);

                UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(
                                nonExistentTransactionId,
                                TransactionType.EXPENSE,
                                BigDecimal.valueOf(200),
                                "Updated Transaction",
                                LocalDate.of(2026, 4, 12),
                                category.getId());

                assertThrows(com.financas.projeto.transaction.exception.TransactionNotFoundException.class, () -> {
                        transactionService.updateTransaction(updateRequest, user);
                });
        }

        @Test
        void shouldThrowWhenUpdatingTransactionWithUnauthorizedUser() {
                User user1 = TestDataFactory.createUser(userRepository);
                User user2 = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user1,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(
                                transaction.getId(),
                                TransactionType.EXPENSE,
                                BigDecimal.valueOf(200),
                                "Updated Transaction",
                                LocalDate.of(2026, 4, 12),
                                category.getId());

                assertThrows(com.financas.projeto.transaction.exception.TransactionUnauthorizedException.class,
                                () -> {
                                        transactionService.updateTransaction(updateRequest, user2);
                                });
        }

        @Test
        void shouldThrowWhenUpdatingTransactionWithNonExistentCategory() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                UUID nonExistentCategoryId = UUID.randomUUID();

                UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(
                                transaction.getId(),
                                TransactionType.EXPENSE,
                                BigDecimal.valueOf(200),
                                "Updated Transaction",
                                LocalDate.of(2026, 4, 12),
                                nonExistentCategoryId);

                assertThrows(CategoryNotFoundException.class, () -> {
                        transactionService.updateTransaction(updateRequest, user);
                });
        }

        @Test
        void shouldLoadCategoryWithTransaction() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user, category, BigDecimal.valueOf(100),
                                LocalDate.now(), "Test", TransactionType.INCOME);

                transactionRepository.save(transaction);

                Transaction loaded = transactionRepository
                                .findByIdWithCategory(transaction.getId())
                                .orElseThrow();

                assertNotNull(loaded.getCategory());
                assertEquals(category.getName(), loaded.getCategory().getName());
        }

        @Test
        void shouldDeleteTransactionSuccessfully() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                DeleteTransactionRequest deleteRequest = new DeleteTransactionRequest(transaction.getId());

                transactionService.deleteTransaction(deleteRequest, user);

                assertTrue(transactionRepository.findByIdWithCategory(transaction.getId()).isEmpty());
        }

        @Test
        void shouldThrowWhenDeletingNonExistentTransaction() {
                User user = TestDataFactory.createUser(userRepository);
                UUID nonExistentTransactionId = UUID.randomUUID();

                DeleteTransactionRequest deleteRequest = new DeleteTransactionRequest(nonExistentTransactionId);

                assertThrows(TransactionNotFoundException.class, () -> {
                        transactionService.deleteTransaction(deleteRequest, user);
                });
        }

        @Test
        void shouldThrowWhenDeletingTransactionWithUnauthorizedUser() {
                User user1 = TestDataFactory.createUser(userRepository);
                User user2 = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                Transaction transaction = TestDataFactory.createTransaction(
                                user1,
                                category,
                                BigDecimal.valueOf(100),
                                LocalDate.of(2026, 4, 10),
                                "Test Transaction",
                                TransactionType.INCOME);

                transactionRepository.save(transaction);

                DeleteTransactionRequest deleteRequest = new DeleteTransactionRequest(transaction.getId());

                assertThrows(com.financas.projeto.transaction.exception.TransactionUnauthorizedException.class,
                                () -> {
                                        transactionService.deleteTransaction(deleteRequest, user2);
                                });
        }

        @Test
        void shouldRespectPagination() {
                User user = TestDataFactory.createUser(userRepository);
                Category category = TestDataFactory.createCategory(categoryRepository);

                for (int i = 0; i < 15; i++) {
                        transactionRepository.save(
                                        TestDataFactory.createTransaction(
                                                        user, category, BigDecimal.TEN,
                                                        LocalDate.now(), "T" + i, TransactionType.INCOME));
                }

                Page<TransactionResponse> page = transactionService
                                .getAllTransactionsByUserId(user.getId(), PageRequest.of(0, 10));

                assertEquals(10, page.getContent().size());
                assertEquals(2, page.getTotalPages());
        }
}
