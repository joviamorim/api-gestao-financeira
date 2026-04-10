package com.financas.projeto.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.transaction.exception.TransactionNotFoundException;
import com.financas.projeto.transaction.exception.TransactionUnauthorizedException;
import com.financas.projeto.transaction.mapper.TransactionMapper;
import com.financas.projeto.transaction.repository.TransactionRepository;
import com.financas.projeto.user.entity.User;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private TransactionMapper transactionMapper;

        @InjectMocks
        private TransactionService transactionService;

        @Test
        void shouldCreateTransactionSuccessfully() {
                // Arrange
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 4, 10);

                RegisterTransactionRequest request = new RegisterTransactionRequest(
                                TransactionType.EXPENSE,
                                new BigDecimal("50.00"),
                                "Almoço",
                                date,
                                categoryId);

                User user = new User();

                Category category = new Category();
                category.setName("Alimentação");

                Transaction transaction = new Transaction();

                TransactionResponse response = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.EXPENSE,
                                new BigDecimal("50.00"),
                                "Almoço",
                                date,
                                "Alimentação");

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.of(category));

                when(transactionMapper.toEntity(request))
                                .thenReturn(transaction);

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(response);

                // Act
                TransactionResponse result = transactionService.createTransaction(request, user);

                // Assert
                assertNotNull(result);
                assertEquals("Almoço", result.description());
                assertEquals(new BigDecimal("50.00"), result.amount());
                assertEquals(TransactionType.EXPENSE, result.type());
                assertEquals(date, result.date());
                assertEquals("Alimentação", result.categoryName());

                verify(transactionRepository).save(transaction);
        }

        @Test
        void shouldThrowWhenCategoryNotFoundOnCreate() {
                // Arrange
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 2, 1);

                RegisterTransactionRequest request = new RegisterTransactionRequest(TransactionType.EXPENSE,
                                new BigDecimal("50.00"), "Almoço", date, categoryId);

                User user = new User();

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(CategoryNotFoundException.class,
                                () -> {
                                        transactionService.createTransaction(request, user);
                                });

                verify(transactionRepository, never()).save(any());
        }

        @Test
        void shouldUpdateTransactionSuccessfully() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 3, 15);

                UpdateTransactionRequest request = new UpdateTransactionRequest(transactionId, TransactionType.EXPENSE,
                                new BigDecimal("50.00"), "Almoço", date, categoryId);

                User user = new User(userId);
                User transactionUser = new User(userId);

                Category category = new Category();
                category.setName("Alimentação");

                Transaction transaction = new Transaction();
                transaction.setUser(transactionUser);

                TransactionResponse response = new TransactionResponse(
                                transactionId,
                                TransactionType.EXPENSE,
                                new BigDecimal("50.00"),
                                "Almoço",
                                date,
                                "Alimentação");

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.of(category));

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(response);

                // Act
                TransactionResponse result = transactionService.updateTransaction(request, user);

                // Assert
                ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
                verify(transactionRepository).save(captor.capture());

                Transaction saved = captor.getValue();

                assertEquals("Almoço", saved.getDescription());
                assertEquals(new BigDecimal("50.00"), saved.getAmount());
                assertEquals(date, saved.getDate());
                assertEquals(category, saved.getCategory());

                assertNotNull(result);
                assertEquals(TransactionType.EXPENSE, result.type());
                assertEquals("Alimentação", result.categoryName());

                verify(transactionRepository).findById(transactionId);
                verify(categoryRepository).findById(categoryId);
        }

        @Test
        void shouldThrowWhenTransactionNotFoundOnUpdate() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 3, 15);

                UpdateTransactionRequest request = new UpdateTransactionRequest(transactionId, TransactionType.EXPENSE,
                                new BigDecimal("50.00"), "Almoço", date, categoryId);

                User user = new User(userId);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(TransactionNotFoundException.class,
                                () -> {
                                        transactionService.updateTransaction(request, user);
                                });

                verify(transactionRepository).findById(transactionId);
                verify(transactionRepository, never()).save(any());
        }

        @Test
        void shouldThrowWhenTransactionUnauthorizedOnUpdate() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();
                UUID otherUserId = UUID.randomUUID();
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 3, 15);

                UpdateTransactionRequest request = new UpdateTransactionRequest(transactionId, TransactionType.EXPENSE,
                                new BigDecimal("50.00"), "Almoço", date, categoryId);

                User user = new User(userId);
                User transactionUser = new User(otherUserId);

                Transaction transaction = new Transaction();
                transaction.setUser(transactionUser);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                // Act & Assert
                assertThrows(TransactionUnauthorizedException.class,
                                () -> {
                                        transactionService.updateTransaction(request, user);
                                });

                verify(transactionRepository).findById(transactionId);
                verify(transactionRepository, never()).save(any());
                verify(categoryRepository, never()).findById(any(UUID.class));
        }

        @Test
        void shouldThrowWhenCategoryNotFoundOnUpdate() {
                // Arrange
                UUID transactionUuid = UUID.randomUUID();
                UUID userId = UUID.randomUUID();
                UUID categoryId = UUID.randomUUID();
                LocalDate date = LocalDate.of(2026, 3, 15);

                UpdateTransactionRequest request = new UpdateTransactionRequest(transactionUuid,
                                TransactionType.EXPENSE,
                                new BigDecimal("50.00"), "Almoço", date, categoryId);

                User user = new User(userId);
                User transactionUser = new User(userId);

                Transaction transaction = new Transaction();
                transaction.setUser(transactionUser);

                when(transactionRepository.findById(transactionUuid))
                                .thenReturn(Optional.of(transaction));

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(CategoryNotFoundException.class,
                                () -> {
                                        transactionService.updateTransaction(request, user);
                                });

                verify(transactionRepository).findById(transactionUuid);
                verify(categoryRepository).findById(categoryId);
                verify(transactionRepository, never()).save(any());
        }

        @Test
        void shouldDeleteTransactionSuccessfully() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();

                DeleteTransactionRequest request = new DeleteTransactionRequest(transactionId);

                User user = new User(userId);
                User transactionUser = new User(userId);

                Transaction transaction = new Transaction();
                transaction.setUser(transactionUser);

                TransactionResponse response = new TransactionResponse(
                                transactionId,
                                TransactionType.EXPENSE,
                                new BigDecimal("50.00"),
                                "Almoço",
                                LocalDate.of(2026, 3, 15),
                                "Alimentação");

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(response);

                // Act
                TransactionResponse result = transactionService.deleteTransaction(request, user);

                // Assert
                assertNotNull(result);
                assertEquals(transactionId, result.id());
                assertEquals("Almoço", result.description());
                assertEquals(new BigDecimal("50.00"), result.amount());
                assertEquals(TransactionType.EXPENSE, result.type());
                assertEquals(LocalDate.of(2026, 3, 15), result.date());
                assertEquals("Alimentação", result.categoryName());

                verify(transactionRepository).findById(transactionId);
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldThrowWhenTransactionNotFoundOnDelete() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();

                DeleteTransactionRequest request = new DeleteTransactionRequest(transactionId);

                User user = new User(userId);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(TransactionNotFoundException.class,
                                () -> {
                                        transactionService.deleteTransaction(request, user);
                                });

                verify(transactionRepository).findById(transactionId);
                verify(transactionRepository, never()).delete(any());
        }

        @Test
        void shouldThrowWhenTransactionUnauthorizedOnDelete() {
                // Arrange
                UUID transactionId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();
                UUID otherUserId = UUID.randomUUID();

                DeleteTransactionRequest request = new DeleteTransactionRequest(transactionId);

                User user = new User(userId);
                User transactionUser = new User(otherUserId);

                Transaction transaction = new Transaction();
                transaction.setUser(transactionUser);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                // Act & Assert
                assertThrows(TransactionUnauthorizedException.class,
                                () -> {
                                        transactionService.deleteTransaction(request, user);
                                });

                verify(transactionRepository).findById(transactionId);
                verify(transactionRepository, never()).delete(any());

        }
}
