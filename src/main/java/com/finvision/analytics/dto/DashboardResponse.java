package com.finvision.analytics.dto;

import com.finvision.transaction.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal balance;

    private Long totalTransactions;

    private Long totalBudgets;

    private List<CategoryExpenseResponse> topExpenseCategories;

    private List<TransactionResponse> recentTransactions;
}
