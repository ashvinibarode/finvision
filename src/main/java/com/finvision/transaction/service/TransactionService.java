package com.finvision.transaction.service;

import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse addTransaction(TransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);

    TransactionResponse getTransactionById(Long id);

    List<TransactionResponse> getAllTransactions();
}