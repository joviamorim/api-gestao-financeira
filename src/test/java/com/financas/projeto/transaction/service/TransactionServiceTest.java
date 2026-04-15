package com.financas.projeto.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        @Test
        void shouldGetAllTransactionsByUserIdSuccessfully() {
                // Arrange
                UUID userId = UUID.randomUUID();
                Pageable pageable = Pageable.unpaged();

                Transaction transaction = new Transaction();

                UUID id = UUID.randomUUID();
                TransactionResponse transactionResponse = new TransactionResponse(
                                id,
                                TransactionType.EXPENSE,
                                BigDecimal.TEN,
                                "desc",
                                LocalDate.now(),
                                "food");

                Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

                when(transactionRepository.findAllByUserId(userId, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(transactionResponse);

                // Act
                Page<TransactionResponse> result = transactionService.getAllTransactionsByUserId(userId, pageable);
                TransactionResponse response = result.getContent().get(0);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(id, response.id());

                verify(transactionRepository).findAllByUserId(userId, pageable);
                verify(transactionMapper).toResponse(transaction);
        }

        @Test
        void shouldReturnEmptyPageWhenUserHasNoTransactions() {
                // Arrange
                UUID userId = UUID.randomUUID();
                Pageable pageable = Pageable.unpaged();

                Page<Transaction> emptyPage = Page.empty();

                when(transactionRepository.findAllByUserId(userId, pageable))
                                .thenReturn(emptyPage);

                // Act
                Page<TransactionResponse> result = transactionService.getAllTransactionsByUserId(userId, pageable);

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());
                assertEquals(0, result.getContent().size());

                verify(transactionRepository).findAllByUserId(userId, pageable);
                verifyNoInteractions(transactionMapper);
        }

        @Test
        void shouldMapAllTransactionsWhenMultipleItemsExist() {
                // Arrange
                UUID userId = UUID.randomUUID();
                Pageable pageable = Pageable.unpaged();

                Transaction transaction1 = new Transaction();
                Transaction transaction2 = new Transaction();

                TransactionResponse response1 = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.EXPENSE,
                                BigDecimal.ONE,
                                "desc1",
                                LocalDate.now(),
                                "food");

                TransactionResponse response2 = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.INCOME,
                                BigDecimal.TEN,
                                "desc2",
                                LocalDate.now(),
                                "salary");

                Page<Transaction> transactions = new PageImpl<>(List.of(transaction1, transaction2));

                when(transactionRepository.findAllByUserId(userId, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction1)).thenReturn(response1);
                when(transactionMapper.toResponse(transaction2)).thenReturn(response2);

                // Act
                Page<TransactionResponse> result = transactionService.getAllTransactionsByUserId(userId, pageable);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());

                List<TransactionResponse> content = result.getContent();

                assertEquals(response1, content.get(0));
                assertEquals(response2, content.get(1));

                verify(transactionRepository).findAllByUserId(userId, pageable);
                verify(transactionMapper).toResponse(transaction1);
                verify(transactionMapper).toResponse(transaction2);
                verifyNoMoreInteractions(transactionMapper);
        }

        @Test
        void shouldPreservePaginationMetadataWhenMappingTransactions() {
                // Arrange
                UUID userId = UUID.randomUUID();
                Pageable pageable = PageRequest.of(0, 2);

                Transaction transaction1 = new Transaction();
                Transaction transaction2 = new Transaction();

                TransactionResponse response1 = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.EXPENSE,
                                BigDecimal.ONE,
                                "desc1",
                                LocalDate.now(),
                                "food");

                TransactionResponse response2 = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.INCOME,
                                BigDecimal.TEN,
                                "desc2",
                                LocalDate.now(),
                                "salary");

                List<Transaction> content = List.of(transaction1, transaction2);
                Integer totalElements = 10;
                Page<Transaction> transactions = new PageImpl<>(
                                content,
                                pageable,
                                totalElements);

                when(transactionRepository.findAllByUserId(userId, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction1)).thenReturn(response1);
                when(transactionMapper.toResponse(transaction2)).thenReturn(response2);

                // Act
                Page<TransactionResponse> result = transactionService.getAllTransactionsByUserId(userId, pageable);

                // Assert
                assertEquals(2, result.getContent().size());
                assertEquals(10, result.getTotalElements());
                assertEquals(5, result.getTotalPages());
                assertEquals(0, result.getNumber());
                assertEquals(2, result.getSize());

                verify(transactionRepository).findAllByUserId(userId, pageable);
                verify(transactionMapper).toResponse(transaction1);
                verify(transactionMapper).toResponse(transaction2);
        }

        @Test
        void shouldGetTransactionsByUserIdAndDateBetweenSuccessfully() {
                // Arrange
                UUID userId = UUID.randomUUID();
                LocalDate startDate = LocalDate.of(2026, 1, 1);
                LocalDate endDate = LocalDate.of(2026, 2, 27);
                Pageable pageable = Pageable.unpaged();

                Transaction transaction = new Transaction();

                Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

                TransactionResponse transactionResponse = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.EXPENSE,
                                BigDecimal.TEN,
                                "desc",
                                LocalDate.now(),
                                "food");

                when(transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(transactionResponse);

                // Act
                Page<TransactionResponse> result = transactionService.getTransactionsByUserIdAndDateBetween(
                                userId, startDate, endDate, pageable);
                TransactionResponse response = result.getContent().get(0);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(transactionResponse, response);
                verify(transactionRepository).findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
                verify(transactionMapper).toResponse(transaction);
        }

        @Test
        void shouldGetTransactionsByUserIdAndCategorySuccessfully() {
                // Arrange
                UUID userId = UUID.randomUUID();
                UUID categoryId = UUID.randomUUID();
                Pageable pageable = Pageable.unpaged();

                Transaction transaction = new Transaction();
                Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

                TransactionResponse transactionResponse = new TransactionResponse(
                                UUID.randomUUID(),
                                TransactionType.EXPENSE,
                                BigDecimal.TEN,
                                "desc",
                                LocalDate.now(),
                                "food");

                when(transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(transactionResponse);

                // Act
                Page<TransactionResponse> result = transactionService.getTransactionsByUserIdAndCategory(
                                userId, categoryId, pageable);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(transactionResponse, result.getContent().get(0));
                verify(transactionRepository).findByUserIdAndCategoryId(userId, categoryId, pageable);
                verify(transactionMapper).toResponse(transaction);
        }

        @Test
        void shouldGetTransactionsByUserIdAndTypeSuccessfully() {
                UUID userId = UUID.randomUUID();
                TransactionType type = TransactionType.EXPENSE;
                Pageable pageable = Pageable.unpaged();

                Transaction transaction = new Transaction();
                Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

                TransactionResponse transactionResponse = new TransactionResponse(
                                UUID.randomUUID(),
                                type,
                                BigDecimal.TEN,
                                "desc",
                                LocalDate.now(),
                                "food");

                when(transactionRepository.findByUserIdAndType(userId, type, pageable))
                                .thenReturn(transactions);

                when(transactionMapper.toResponse(transaction))
                                .thenReturn(transactionResponse);

                // Act
                Page<TransactionResponse> result = transactionService.getTransactionsByUserIdAndType(
                                userId, type, pageable);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(transactionResponse, result.getContent().get(0));
                verify(transactionRepository).findByUserIdAndType(userId, type, pageable);
                verify(transactionMapper).toResponse(transaction);

        }
}
