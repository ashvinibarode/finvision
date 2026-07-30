package com.finvision.transaction.service;

import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse addTransaction(String email, TransactionRequest request);

    TransactionResponse updateTransaction(Long id, String email, TransactionRequest request);

    void deleteTransaction(Long id, String email);

    TransactionResponse getTransactionById(Long id, String email);

    List<TransactionResponse> getAllTransactions(String email);
}