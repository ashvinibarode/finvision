package com.finvision.analytics.service.impl;

import com.finvision.analytics.dto.DashboardResponse;
import com.finvision.analytics.service.AnalyticsService;
import com.finvision.budget.repository.BudgetRepository;
import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal totalIncome = transactionRepository.getTotalIncome(
                user,
                CategoryType.INCOME
        );

        BigDecimal totalExpense = transactionRepository.getTotalExpense(
                user,
                CategoryType.EXPENSE
        );

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBudgets(budgetRepository.countByUser(user))
                .totalTransactions(transactionRepository.countByUser(user))
                .build();
    }
}