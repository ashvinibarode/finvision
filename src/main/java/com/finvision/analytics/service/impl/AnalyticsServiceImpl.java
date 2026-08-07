package com.finvision.analytics.service.impl;

import com.finvision.analytics.dto.CategoryExpenseResponse;
import com.finvision.analytics.dto.DashboardResponse;
import com.finvision.analytics.service.AnalyticsService;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.entity.Transaction;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.finvision.budget.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finvision.category.entity.CategoryType;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        BigDecimal income = transactionRepository.getTotalIncome(
                user,
                CategoryType.INCOME
        );

        BigDecimal expense = transactionRepository.getTotalExpense(
                user,
                CategoryType.EXPENSE
        );


        BigDecimal balance = income.subtract(expense);

        Long totalTransactions = transactionRepository.countByUser(user);

        Long totalBudgets = budgetRepository.countByUser(user);

        List<CategoryExpenseResponse> topCategories =
                transactionRepository.getTopExpenseCategories(
                                user,
                                CategoryType.EXPENSE
                        )
                        .stream()
                        .map(obj -> CategoryExpenseResponse.builder()
                                .category((String) obj[0])
                                .amount((BigDecimal) obj[1])
                                .build())
                        .toList();

        List<TransactionResponse> recentTransactions =
                transactionRepository.findTop5ByUserOrderByTransactionDateDesc(user)
                        .stream()
                        .map(this::mapTransaction)
                        .toList();

        return DashboardResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .balance(balance)
                .totalTransactions(totalTransactions)
                .totalBudgets(totalBudgets)
                .topExpenseCategories(topCategories)
                .recentTransactions(recentTransactions)
                .build();
    }

    private TransactionResponse mapTransaction(Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getCategory().getType())
                .category(transaction.getCategory().getName())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}