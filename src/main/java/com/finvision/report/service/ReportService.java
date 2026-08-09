package com.finvision.report.service;

import com.finvision.report.dto.ReportSummaryResponse;

public interface ReportService {

    ReportSummaryResponse getMonthlySummary(
            String email,
            String month
    );
}
