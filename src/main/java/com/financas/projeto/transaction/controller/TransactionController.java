package com.financas.projeto.transaction.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financas.projeto.common.response.ApiResponse;
import com.financas.projeto.transaction.dto.DeleteTransactionRequest;
import com.financas.projeto.transaction.dto.RegisterTransactionRequest;
import com.financas.projeto.transaction.dto.TransactionResponse;
import com.financas.projeto.transaction.dto.TransactionValueByTypeResponse;
import com.financas.projeto.transaction.dto.UpdateTransactionRequest;
import com.financas.projeto.transaction.entity.TransactionType;
import com.financas.projeto.transaction.service.TransactionService;
import com.financas.projeto.user.entity.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

        private final TransactionService transactionService;

        public TransactionController(TransactionService transactionService) {
                this.transactionService = transactionService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactionsByUserId(
                        @AuthenticationPrincipal User user,
                        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<TransactionResponse> transactions = transactionService.getAllTransactionsByUserId(user.getId(),
                                pageable);

                return ResponseEntity.ok(ApiResponse.success(transactions));
        }

        @GetMapping("/filter-by-month")
        public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByUserIdAndMonth(
                        @AuthenticationPrincipal User user,
                        @RequestParam YearMonth month,
                        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
                LocalDate startDate = month.atDay(1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                Page<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndDateBetween(
                                user.getId(),
                                startDate,
                                endDate,
                                pageable);

                return ResponseEntity.ok(ApiResponse.success(transactions));
        }

        @GetMapping("/filter-by-date-range")
        public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByUserIdAndDateBetween(
                        @AuthenticationPrincipal User user,
                        @RequestParam LocalDate startDate,
                        @RequestParam LocalDate endDate,
                        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndDateBetween(
                                user.getId(),
                                startDate,
                                endDate,
                                pageable);

                return ResponseEntity.ok(ApiResponse.success(transactions));
        }

        @GetMapping("/filter-by-category")
        public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByUserIdAndCategoryId(
                        @AuthenticationPrincipal User user,
                        @RequestParam UUID categoryId,
                        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndCategory(
                                user.getId(),
                                categoryId,
                                pageable);

                return ResponseEntity.ok(ApiResponse.success(transactions));
        }

        @GetMapping("/filter-by-type")
        public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByUserIdAndType(
                        @AuthenticationPrincipal User user,
                        @RequestParam TransactionType type,
                        @ParameterObject @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndType(
                                user.getId(),
                                type,
                                pageable);

                return ResponseEntity.ok(ApiResponse.success(transactions));
        }

        @GetMapping("/total-value-by-type")
        public ResponseEntity<ApiResponse<TransactionValueByTypeResponse>> getTotalValueByType(
                        @AuthenticationPrincipal User user,
                        @RequestParam TransactionType type) {
                TransactionValueByTypeResponse totalValue = transactionService
                                .getTotalValueByUserIdAndType(user.getId(), type);
                return ResponseEntity.ok(ApiResponse.success(totalValue, "Total value retrieved successfully"));
        }

        @PostMapping("/create")
        public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
                        @AuthenticationPrincipal User user,
                        @RequestBody @Valid RegisterTransactionRequest request) {
                TransactionResponse transaction = transactionService.createTransaction(request, user);

                return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction created successfully"));
        }

        @PutMapping("/update")
        public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
                        @AuthenticationPrincipal User user,
                        @RequestBody @Valid UpdateTransactionRequest request) {
                TransactionResponse transaction = transactionService.updateTransaction(request, user);

                return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction updated successfully"));
        }

        @DeleteMapping("/delete")
        public ResponseEntity<ApiResponse<TransactionResponse>> deleteTransaction(
                        @AuthenticationPrincipal User user,
                        @RequestBody @Valid DeleteTransactionRequest request) {
                TransactionResponse transaction = transactionService.deleteTransaction(request, user);

                return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction deleted successfully"));
        }
}
