package com.finvision.transaction.service.impl;

import com.finvision.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Override
    public TransactionResponse addTransaction(TransactionRequest request) {
        return null;
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        return null;
    }

    @Override
    public void deleteTransaction(Long id) {

    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        return null;
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return List.of();
    }
}