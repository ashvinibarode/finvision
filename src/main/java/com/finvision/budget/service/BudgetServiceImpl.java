package com.finvision.budget.service.impl;

import com.finvision.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finvision.budget.repository.BudgetRepository;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.user.repository.UserRepository;
import com.finvision.budget.dto.BudgetResponse;
import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.entity.Budget;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.user.entity.User;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // CRUD methods yahan aayenge
    @Override
    public BudgetResponse createBudget(String email, BudgetRequest request) {
        return null;
    }

    @Override
    public BudgetResponse updateBudget(Long id, String email, BudgetRequest request) {
        return null;
    }

    @Override
    public void deleteBudget(Long id, String email) {

    }

    @Override
    public BudgetResponse getBudgetById(Long id, String email) {
        return null;
    }

    @Override
    public List<BudgetResponse> getAllBudgets(String email) {
        return List.of();
    }

    // ---------------- Helper Methods ----------------

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .category(budget.getCategory().getName())
                .build();
    }
}
