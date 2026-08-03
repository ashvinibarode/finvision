package com.finvision.budget.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetResponse {

    private Long id;
    private BigDecimal amount;
    private String month;
    private String category;
}