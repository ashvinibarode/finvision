package com.finvision.budget.service.impl;


import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.dto.BudgetResponse;
import com.finvision.budget.dto.BudgetSummaryResponse;
import com.finvision.budget.entity.Budget;
import com.finvision.budget.repository.BudgetRepository;
import com.finvision.budget.service.BudgetService;
import com.finvision.category.entity.Category;
import com.finvision.category.entity.CategoryType;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.common.exception.DuplicateResourceException;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.finvision.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.math.BigDecimal;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public BudgetResponse createBudget(String email, BudgetRequest request) {

        User user = getUser(email);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException(
                    "Budget can only be created for expense categories");
        }

        budgetRepository.findByUserAndCategoryAndMonth(
                user,
                category,
                request.getMonth()
        ).ifPresent(budget -> {
            throw new DuplicateResourceException(
                    "Budget already exists for this category and month");
        });

        Budget budget = Budget.builder()
                .amount(request.getAmount())
                .month(request.getMonth())
                .category(category)
                .user(user)
                .build();

        budgetRepository.save(budget);

        return mapToResponse(budget);
    }

    @Override
    public BudgetResponse updateBudget(Long id,
                                       String email,
                                       BudgetRequest request) {

        User user = getUser(email);

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException(
                    "Budget can only be created for expense categories");
        }

        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setCategory(category);

        budgetRepository.save(budget);

        return mapToResponse(budget);
    }

    @Override
    public void deleteBudget(Long id, String email) {

        User user = getUser(email);

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        budgetRepository.delete(budget);
    }

    @Override
    public BudgetResponse getBudgetById(Long id, String email) {

        User user = getUser(email);

        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        return mapToResponse(budget);
    }

    @Override
    public List<BudgetResponse> getAllBudgets(String email) {

        User user = getUser(email);

        return budgetRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
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


    @Override
    public BudgetSummaryResponse getBudgetSummary(Long budgetId,
                                                  String email) {

        User user = getUser(email);

        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

         BigDecimal spent =
                 transactionRepository.getMonthlyExpense(
                         user,
                         budget.getCategory(),
                         CategoryType.EXPENSE,
                         budget.getMonth());
        BigDecimal remaining =
                budget.getAmount().subtract(spent);

        double percentage = spent
                .divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return BudgetSummaryResponse.builder()
                .category(budget.getCategory().getName())
                .month(budget.getMonth())
                .budget(budget.getAmount())
                .spent(spent)
                .remaining(remaining)
                .utilizationPercentage(percentage)
                .exceeded(spent.compareTo(budget.getAmount()) > 0)
                .build();
    }
}