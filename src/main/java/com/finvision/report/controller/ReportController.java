package com.finvision.report.controller;

import com.finvision.report.dto.ReportSummaryResponse;
import com.finvision.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {

        return ResponseEntity.ok(
                reportService.getReport(
                        authentication.getName(),
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {

        byte[] pdf = reportService.generatePdfReport(
                authentication.getName(),
                startDate,
                endDate
        );

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=finvision-report.pdf"
                )
                .header(
                        "Content-Type",
                        "application/pdf"
                )
                .body(pdf);
    }
}