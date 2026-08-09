package com.finvision.report.service.impl;

import com.finvision.report.dto.ReportSummaryResponse;
import com.finvision.report.service.ReportService;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import com.finvision.category.entity.CategoryType;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ReportSummaryResponse getReport(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        BigDecimal income =
                transactionRepository.getIncomeBetweenDates(
                        user,
                        CategoryType.INCOME,
                        startDate,
                        endDate);

        BigDecimal expense =
                transactionRepository.getExpenseBetweenDates(
                        user,
                        CategoryType.INCOME,
                        startDate,
                        endDate);

        Long transactionCount =
                transactionRepository.countTransactionsBetweenDates(
                        user,
                        startDate,
                        endDate);

        return ReportSummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .totalTransactions(transactionCount)
                .month(startDate + " to " + endDate)
                .build();
    }

}
