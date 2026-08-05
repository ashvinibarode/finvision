package com.finvision.analytics.service;

import com.finvision.analytics.dto.DashboardResponse;

public interface AnalyticsService {

    DashboardResponse getDashboard(String email);

}