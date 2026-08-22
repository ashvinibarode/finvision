package com.finvision.transaction.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionFilterRequest {

    private String search;

    private Long categoryId;

    private LocalDate fromDate;

    private LocalDate toDate;
}