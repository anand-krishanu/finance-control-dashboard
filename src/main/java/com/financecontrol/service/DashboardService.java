package com.financecontrol.service;

import com.financecontrol.dto.DashboardSummary;
import com.financecontrol.dto.TrendData;
import com.financecontrol.model.User;
import com.financecontrol.repository.FinancialRecordRepository;
import com.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;import java.time.LocalDate;
import java.util.List;
/**
 * Does the heavy lifting to figure out how much money we actually have.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final FinancialRecordRepository repository;
    private final UserRepository userRepository;

    /**
     * Finds the currently logged in user to pull their custom math.
     */
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User missing lol?"));
    }

    /**
     * Crunches the numbers to give us a quick breakdown of your cash.
     *
     * @return the final math summary of all our money
     */
    public DashboardSummary getSummary() {
        log.info("Calculating dashboard summary logic started.");

        Long userId = getCurrentUserId();

        BigDecimal income = repository.getTotalIncome(userId);
        if (income == null) income = BigDecimal.ZERO;

        BigDecimal expense = repository.getTotalExpense(userId);
        if (expense == null) expense = BigDecimal.ZERO;

        BigDecimal netBalance = income.subtract(expense);
        
        // Grab trends over the last 30 days
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<TrendData> trends = repository.getTrendData(userId, thirtyDaysAgo);

        return new DashboardSummary(income, expense, netBalance, trends);
    }
}
