package com.finvision.report.service.impl;

import com.finvision.category.entity.CategoryType;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.report.dto.CategoryExpenseResponse;
import com.finvision.report.dto.ReportSummaryResponse;
import com.finvision.report.dto.ReportTransactionResponse;
import com.finvision.report.service.ReportService;
import com.finvision.transaction.entity.Transaction;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }


        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));


        // Calculate total income
        BigDecimal income =
                transactionRepository.getIncomeBetweenDates(
                        user,
                        CategoryType.INCOME,
                        startDate,
                        endDate);


        // Calculate total expense
        BigDecimal expense =
                transactionRepository.getExpenseBetweenDates(
                        user,
                        CategoryType.EXPENSE,
                        startDate,
                        endDate);


        // Count transactions
        Long transactionCount =
                transactionRepository
                        .countTransactionsBetweenDates(
                                user,
                                startDate,
                                endDate);


        // Category-wise expense data
        List<Object[]> categoryData =
                transactionRepository
                        .getCategoryExpenseSummary(
                                user,
                                CategoryType.EXPENSE,
                                startDate,
                                endDate);


        List<CategoryExpenseResponse> categoryExpenses =
                new ArrayList<>();


        for (Object[] row : categoryData) {

            categoryExpenses.add(
                    CategoryExpenseResponse.builder()
                            .categoryName(
                                    (String) row[0]
                            )
                            .amount(
                                    (BigDecimal) row[1]
                            )
                            .build()
            );
        }


        // Get transactions for selected period
        List<Transaction> transactions =
                transactionRepository
                        .findByUserAndTransactionDateBetween(
                                user,
                                startDate,
                                endDate);


        // Convert transactions to response DTO
        List<ReportTransactionResponse> transactionResponses =
                transactions.stream()
                        .map(transaction ->
                                ReportTransactionResponse.builder()
                                        .title(
                                                transaction.getTitle()
                                        )
                                        .categoryName(
                                                transaction
                                                        .getCategory()
                                                        .getName()
                                        )
                                        .amount(
                                                transaction.getAmount()
                                        )
                                        .transactionDate(
                                                transaction
                                                        .getTransactionDate()
                                        )
                                        .type(
                                                transaction
                                                        .getCategory()
                                                        .getType()
                                                        .toString()
                                        )
                                        .build()
                        )
                        .toList();


        // Build report response
        return ReportSummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .totalTransactions(transactionCount)
                .month(startDate + " to " + endDate)
                .categoryExpenses(categoryExpenses)
                .transactions(transactionResponses)
                .build();
    }


    @Override
    public byte[] generatePdfReport(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        // Get summary
        ReportSummaryResponse summary =
                getReport(
                        email,
                        startDate,
                        endDate
                );


        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));


        // Get transactions for selected period
        List<Transaction> transactions =
                transactionRepository
                        .findByUserAndTransactionDateBetween(
                                user,
                                startDate,
                                endDate);


        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            Document document =
                    new Document();


            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();


            // Fonts
            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );


            Font headingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );


            // Title
            document.add(
                    new Paragraph(
                            "FinVision Financial Report",
                            titleFont
                    )
            );


            // Report period
            document.add(
                    new Paragraph(
                            "Period: "
                                    + startDate
                                    + " to "
                                    + endDate
                    )
            );


            document.add(
                    new Paragraph(" ")
            );


            // Financial Summary
            document.add(
                    new Paragraph(
                            "Financial Summary",
                            headingFont
                    )
            );


            PdfPTable summaryTable =
                    new PdfPTable(2);

            summaryTable.setWidthPercentage(100);


            summaryTable.addCell(
                    "Total Income"
            );

            summaryTable.addCell(
                    summary.getTotalIncome()
                            .setScale(2)
                            .toString()
            );


            summaryTable.addCell(
                    "Total Expense"
            );

            summaryTable.addCell(
                    summary.getTotalExpense()
                            .setScale(2)
                            .toString()
            );


            summaryTable.addCell(
                    "Balance"
            );

            summaryTable.addCell(
                    summary.getBalance()
                            .setScale(2)
                            .toString()
            );


            summaryTable.addCell(
                    "Transactions"
            );

            summaryTable.addCell(
                    summary.getTotalTransactions()
                            .toString()
            );


            document.add(summaryTable);


            document.add(
                    new Paragraph(" ")
            );


            // Category-wise Expenses
            document.add(
                    new Paragraph(
                            "Category-wise Expenses",
                            headingFont
                    )
            );


            PdfPTable categoryTable =
                    new PdfPTable(2);

            categoryTable.setWidthPercentage(100);


            categoryTable.addCell(
                    "Category"
            );

            categoryTable.addCell(
                    "Amount"
            );


            for (
                    CategoryExpenseResponse category
                    : summary.getCategoryExpenses()
            ) {

                categoryTable.addCell(
                        category.getCategoryName()
                );

                categoryTable.addCell(
                        category.getAmount()
                                .setScale(2)
                                .toString()
                );
            }


            document.add(categoryTable);


            document.add(
                    new Paragraph(" ")
            );


            // Transactions
            document.add(
                    new Paragraph(
                            "Transactions",
                            headingFont
                    )
            );


            PdfPTable transactionTable =
                    new PdfPTable(5);

            transactionTable.setWidthPercentage(100);


            transactionTable.addCell("Title");
            transactionTable.addCell("Category");
            transactionTable.addCell("Amount");
            transactionTable.addCell("Date");
            transactionTable.addCell("Type");


            for (Transaction transaction : transactions) {

                transactionTable.addCell(
                        transaction.getTitle()
                );

                transactionTable.addCell(
                        transaction.getCategory()
                                .getName()
                );

                transactionTable.addCell(
                        transaction.getAmount()
                                .setScale(2)
                                .toString()
                );

                transactionTable.addCell(
                        transaction.getTransactionDate()
                                .toString()
                );

                transactionTable.addCell(
                        transaction.getCategory()
                                .getType()
                                .toString()
                );
            }


            document.add(transactionTable);


            document.close();


            return outputStream.toByteArray();


        } catch (DocumentException e) {

            throw new RuntimeException(
                    "Failed to generate PDF report",
                    e
            );
        }
    }
}

