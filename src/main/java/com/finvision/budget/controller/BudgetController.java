package com.finvision.budget.controller;

import com.finvision.budget.dto.BudgetSummaryResponse;
import com.finvision.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/{id}/summary")
    public ResponseEntity<BudgetSummaryResponse> summary(
            @PathVariable Long id,
            Authentication authentication){

        return ResponseEntity.ok(
                budgetService.getBudgetSummary(
                        id,
                        authentication.getName()));
    }

}