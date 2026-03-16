package com.financas.projeto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    Page<Transaction> findByUserIdAndDateBetween(
        UUID userId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    Page<Transaction> findByUserIdAndCategoryId(
        UUID userId,
        UUID categoryId,
        Pageable pageable
    );

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
}
