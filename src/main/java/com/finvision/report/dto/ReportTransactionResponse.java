package com.finvision.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReportTransactionResponse {

    private String title;

    private String categoryName;

    private BigDecimal amount;

    private LocalDate transactionDate;

    private String type;
}