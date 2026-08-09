package com.finvision.report.service.impl;

import com.finvision.report.dto.ReportSummaryResponse;
import com.finvision.report.service.ReportService;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ReportSummaryResponse getMonthlySummary(
            String email,
            String month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        BigDecimal income =
                transactionRepository.getMonthlyIncome(user, month);

        BigDecimal expense =
                transactionRepository.getMonthlyExpense(user, month);

        Long count =
                transactionRepository.getMonthlyTransactionCount(
                        user,
                        month);

        return ReportSummaryResponse.builder()
                .month(month)
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .totalTransactions(count)
                .build();
    }
}