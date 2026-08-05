package com.finvision.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryExpenseResponse {

    private String category;

    private BigDecimal amount;
}
