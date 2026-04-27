package com.financas.projeto.transaction.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.financas.projeto.transaction.entity.Transaction;
import com.financas.projeto.transaction.entity.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
        @EntityGraph(attributePaths = { "category" })
        @Query("SELECT t FROM Transaction t WHERE t.id = :id")
        Optional<Transaction> findByIdWithCategory(UUID id);

        @EntityGraph(attributePaths = { "category" })
        Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

        @EntityGraph(attributePaths = { "category" })
        Page<Transaction> findByUserIdAndDateBetween(
                        UUID userId,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable);

        @EntityGraph(attributePaths = { "category" })
        Page<Transaction> findByUserIdAndCategoryId(
                        UUID userId,
                        UUID categoryId,
                        Pageable pageable);

        @Query("""
                        SELECT
                            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),0) -
                            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),0)
                        FROM Transaction t
                        WHERE t.user.id = :userId
                        """)
        BigDecimal getBalance(UUID userId);

        @Query("""
                        SELECT
                            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),0) -
                            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),0)
                        FROM Transaction t
                        WHERE t.user.id = :userId
                        AND t.date >= :startDate
                        AND t.date <= :endDate
                        """)
        BigDecimal getBalanceByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

        @EntityGraph(attributePaths = { "category" })
        Page<Transaction> findByUserIdAndType(
                        UUID userId,
                        TransactionType type,
                        Pageable pageable);

        @Query("""
                        SELECT
                            COALESCE(SUM(t.amount), 0) AS totalValue
                        FROM Transaction t
                        WHERE t.user.id = :userId
                        AND t.type = :type
                        """)
        BigDecimal getTotalValueByUserIdAndType(
                        UUID userId,
                        TransactionType type);
}
