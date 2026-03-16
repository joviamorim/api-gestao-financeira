package com.financas.projeto.balance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.financas.projeto.balance.dto.BalanceResponse;
import com.financas.projeto.user.User;

@RestController
@RequestMapping("/balance")
public class BalanceController {
    
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public BalanceResponse getBalance(
        @AuthenticationPrincipal User user
    ) {
        BigDecimal balance = balanceService.getBalance(user.getId());

        return new BalanceResponse(balance);
    }

    @GetMapping("filter-by-month")
    public BalanceResponse getBalanceByMonth(
        @AuthenticationPrincipal User user,
        YearMonth month
    ) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal balance = balanceService.getBalanceByDateRange(user.getId(), startDate, endDate);

        return new BalanceResponse(balance);
    }
}
