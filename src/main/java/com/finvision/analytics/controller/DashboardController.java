package com.finvision.analytics.controller;

import com.finvision.analytics.dto.DashboardResponse;
import com.finvision.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getDashboard(
                        authentication.getName()));
    }
}