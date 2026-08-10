package com.finvision.report.service.impl;

import com.finvision.category.entity.CategoryType;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.report.dto.ReportSummaryResponse;
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
                        CategoryType.EXPENSE,
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

    @Override
    public byte[] generatePdfReport(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        ReportSummaryResponse summary =
                getReport(email, startDate, endDate);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<Transaction> transactions =
                transactionRepository
                        .findByUserAndTransactionDateBetween(
                                user,
                                startDate,
                                endDate);

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    18
            );

            Font headingFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    12
            );

            document.add(
                    new Paragraph(
                            "FinVision Financial Report",
                            titleFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Period: "
                                    + startDate
                                    + " to "
                                    + endDate
                    )
            );

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Financial Summary",
                            headingFont
                    )
            );

            PdfPTable summaryTable =
                    new PdfPTable(2);

            summaryTable.setWidthPercentage(100);

            summaryTable.addCell("Total Income");
            summaryTable.addCell(
                    summary.getTotalIncome().toString()
            );

            summaryTable.addCell("Total Expense");
            summaryTable.addCell(
                    summary.getTotalExpense().toString()
            );

            summaryTable.addCell("Balance");
            summaryTable.addCell(
                    summary.getBalance().toString()
            );

            summaryTable.addCell("Transactions");
            summaryTable.addCell(
                    summary.getTotalTransactions().toString()
            );

            document.add(summaryTable);

            document.add(new Paragraph(" "));

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
                        transaction.getCategory().getName()
                );

                transactionTable.addCell(
                        transaction.getAmount().toString()
                );

                transactionTable.addCell(
                        transaction.getTransactionDate().toString()
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