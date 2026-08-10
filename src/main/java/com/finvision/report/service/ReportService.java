package com.finvision.report.service;

import com.finvision.report.dto.ReportSummaryResponse;

import java.time.LocalDate;

public interface ReportService {

    ReportSummaryResponse getReport(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );

    byte[] generatePdfReport(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );
}