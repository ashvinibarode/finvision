package com.finvision.budget.service;

import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.dto.BudgetResponse;

import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(
            String email,
            BudgetRequest request
    );

    BudgetResponse updateBudget(
            Long id,
            String email,
            BudgetRequest request
    );

    void deleteBudget(
            Long id,
            String email
    );

    BudgetResponse getBudgetById(
            Long id,
            String email
    );

    List<BudgetResponse> getAllBudgets(
            String email
    );
}