package com.finvision.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryExpenseResponse {

    private String categoryName;

    private BigDecimal amount;
}