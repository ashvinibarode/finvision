package com.finvision.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportSummaryResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal balance;

    private Long totalTransactions;

    private String month;
}
