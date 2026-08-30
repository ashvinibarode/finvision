package com.finvision.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReportSummaryResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal balance;

    private Long totalTransactions;

    private String month;

    private List<CategoryExpenseResponse> categoryExpenses;
}
