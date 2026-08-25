package com.finvision.budget.controller;

import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.dto.BudgetResponse;
import com.finvision.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.finvision.budget.dto.BudgetAnalyticsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;


    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        BudgetResponse response =
                budgetService.createBudget(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getAllBudgets(
                        authentication.getName()
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getBudgetById(
                        id,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {

        budgetService.deleteBudget(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<BudgetAnalyticsResponse>
    getBudgetAnalytics(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getBudgetAnalytics(
                        id,
                        authentication.getName()
                )
        );
    }
}