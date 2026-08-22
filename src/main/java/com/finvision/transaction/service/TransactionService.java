package com.finvision.transaction.service;

import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.dto.TransactionFilterRequest;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponse addTransaction(
            String email,
            TransactionRequest request
    );

    TransactionResponse updateTransaction(
            Long id,
            String email,
            TransactionRequest request
    );

    void deleteTransaction(
            Long id,
            String email
    );

    TransactionResponse getTransactionById(
            Long id,
            String email
    );

    List<TransactionResponse> getAllTransactions(
            String email
    );

    List<TransactionResponse> getTransactionsByType(
            String email,
            CategoryType type
    );

    List<TransactionResponse> getTransactionsByCategory(
            String email,
            Long categoryId
    );

    List<TransactionResponse> searchTransactions(
            String email,
            String keyword
    );

    List<TransactionResponse> getTransactionsBetweenDates(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<TransactionResponse> getTransactions(
            String email,
            int page,
            int size,
            String sortBy,
            String direction,
            TransactionFilterRequest filter
    );
}