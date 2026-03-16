package com.financas.projeto.transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.user.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public Page<TransactionResponse> getAllTransactionsByUserId(
        @AuthenticationPrincipal User user,
        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.getAllTransactionsByUserId(user.getId(), pageable);

        return transactions.map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName()
            ));
    }

    @GetMapping("/filter-by-month")
    public Page<TransactionResponse> getTransactionsByUserIdAndMonth(
        @AuthenticationPrincipal User user,
        @RequestParam YearMonth month,
        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Page<Transaction> transactions = transactionService.getTransactionsByUserIdAndDateBetween(
            user.getId(),
            startDate,
            endDate,
            pageable
        );

        return transactions.map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName()
            ));
    }

    @GetMapping("/filter-by-date-range")
    public Page<TransactionResponse> getTransactionsByUserIdAndDateBetween(
        @AuthenticationPrincipal User user,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate,
        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.getTransactionsByUserIdAndDateBetween(
            user.getId(),
            startDate,
            endDate,
            pageable
        );

        return transactions.map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName()
            ));
    }

    @GetMapping("/filter-by-category")
    public Page<TransactionResponse> getTransactionsByUserIdAndCategoryId(
        @AuthenticationPrincipal User user,
        @RequestParam UUID categoryId,
        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.getTransactionsByUserIdAndCategory(
            user.getId(),
            categoryId,
            pageable
        );

        return transactions.map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName()
            ));
    }

    @PostMapping("/create")
    public TransactionResponse createTransaction(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid RegisterTransactionRequest request
    ) {
        Transaction transaction = transactionService.createTransaction(request, user);

        return new TransactionResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getDate(),
            transaction.getCategory().getName()
        );
    }

    @PutMapping("/update")
    public TransactionResponse updateTransaction(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid UpdateTransactionRequest request
    ) {
        Transaction transaction = transactionService.updateTransaction(request, user);

        return new TransactionResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getDate(),
            transaction.getCategory().getName()
        );
    }

    @DeleteMapping("/delete")
    public TransactionResponse deleteTransaction(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid DeleteTransactionRequest request
    ) {
        Transaction transaction = transactionService.deleteTransaction(request, user);
        
        return new TransactionResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getDate(),
            transaction.getCategory().getName()
        );
    }
}
