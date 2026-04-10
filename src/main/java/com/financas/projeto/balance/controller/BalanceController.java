package com.financas.projeto.balance.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financas.projeto.balance.dto.BalanceResponse;
import com.financas.projeto.balance.service.BalanceService;
import com.financas.projeto.common.response.ApiResponse;
import com.financas.projeto.user.entity.User;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @AuthenticationPrincipal User user) {
        BalanceResponse balance = balanceService.getBalance(user.getId());

        return ResponseEntity.ok(ApiResponse.success(balance));
    }

    @GetMapping("filter-by-month")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalanceByMonth(
            @AuthenticationPrincipal User user,
            YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BalanceResponse balance = balanceService.getBalanceByDateRange(user.getId(), startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}
