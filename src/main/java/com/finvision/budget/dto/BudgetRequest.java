package com.finvision.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetRequest {

    @NotNull(message = "Budget amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Budget amount must be greater than 0"
    )
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Month is required")
    private LocalDate month;
}