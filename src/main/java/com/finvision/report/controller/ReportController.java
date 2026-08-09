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
}