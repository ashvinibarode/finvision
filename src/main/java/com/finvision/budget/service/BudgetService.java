package com.finvision.budget.service;

import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.dto.BudgetResponse;
import com.finvision.budget.dto.BudgetAnalyticsResponse;
import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(
            BudgetRequest request,
            String email
    );

    List<BudgetResponse> getAllBudgets(
            String email
    );

    BudgetResponse getBudgetById(
            Long id,
            String email
    );

    BudgetResponse updateBudget(
            Long id,
            BudgetRequest request,
            String email
    );

    BudgetAnalyticsResponse getBudgetAnalytics(
            Long budgetId,
            String email
    );

    void deleteBudget(
            Long id,
            String email
    );
}