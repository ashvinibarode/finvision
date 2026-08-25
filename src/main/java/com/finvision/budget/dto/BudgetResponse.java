package com.finvision.budget.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BudgetResponse {

    private Long id;

    private BigDecimal amount;

    private LocalDate month;

    private Long categoryId;

    private String categoryName;

    private String categoryType;
}