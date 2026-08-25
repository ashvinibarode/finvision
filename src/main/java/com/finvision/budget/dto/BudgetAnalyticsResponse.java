package com.finvision.budget.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BudgetAnalyticsResponse {

    private Long budgetId;

    private String categoryName;

    private LocalDate month;

    private BigDecimal budgetAmount;

    private BigDecimal spentAmount;

    private BigDecimal remainingAmount;

    private BigDecimal utilizationPercentage;

    private boolean overBudget;
}