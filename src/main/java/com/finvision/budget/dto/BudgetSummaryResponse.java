package com.finvision.budget.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetSummaryResponse {

    private String category;

    private String month;

    private BigDecimal budget;

    private BigDecimal spent;

    private BigDecimal remaining;

    private double utilizationPercentage;

    private boolean exceeded;
}