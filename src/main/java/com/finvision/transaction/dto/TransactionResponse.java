package com.finvision.transaction.dto;

import com.finvision.category.entity.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private CategoryType type;
    private String category;
    private LocalDate transactionDate;
}