package com.finvision.budget.service.impl;

import com.finvision.budget.dto.BudgetRequest;
import com.finvision.budget.dto.BudgetResponse;
import com.finvision.budget.entity.Budget;
import com.finvision.budget.repository.BudgetRepository;
import com.finvision.budget.service.BudgetService;
import com.finvision.category.entity.Category;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finvision.budget.dto.BudgetAnalyticsResponse;
import com.finvision.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Override
    public BudgetResponse createBudget(
            BudgetRequest request,
            String email) {

        User user = getUser(email);

        Category category =
                getCategory(
                        request.getCategoryId(),
                        user
                );


        boolean exists =
                budgetRepository
                        .findByUserAndCategoryAndMonth(
                                user,
                                category,
                                request.getMonth()
                        )
                        .isPresent();


        if (exists) {

            throw new IllegalArgumentException(
                    "Budget already exists for this category and month"
            );
        }


        Budget budget =
                Budget.builder()
                        .amount(request.getAmount())
                        .month(request.getMonth())
                        .category(category)
                        .user(user)
                        .build();


        Budget savedBudget =
                budgetRepository.save(budget);

        return mapToResponse(savedBudget);
    }


    @Override
    public List<BudgetResponse> getAllBudgets(
            String email) {

        User user = getUser(email);

        return budgetRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public BudgetResponse getBudgetById(
            Long id,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Budget not found"
                                ));

        return mapToResponse(budget);
    }


    @Override
    public BudgetResponse updateBudget(
            Long id,
            BudgetRequest request,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Budget not found"
                                ));


        Category category =
                getCategory(
                        request.getCategoryId(),
                        user
                );


        budgetRepository
                .findByUserAndCategoryAndMonth(
                        user,
                        category,
                        request.getMonth()
                )
                .ifPresent(existing -> {

                    if (!existing.getId()
                            .equals(id)) {

                        throw new IllegalArgumentException(
                                "Budget already exists for this category and month"
                        );
                    }
                });


        budget.setAmount(
                request.getAmount()
        );

        budget.setMonth(
                request.getMonth()
        );

        budget.setCategory(
                category
        );


        Budget updatedBudget =
                budgetRepository.save(budget);

        return mapToResponse(updatedBudget);
    }


    @Override
    public void deleteBudget(
            Long id,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Budget not found"
                                ));

        budgetRepository.delete(budget);
    }


    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }


    private Category getCategory(
            Long categoryId,
            User user) {

        return categoryRepository
                .findAccessibleCategory(
                        categoryId,
                        user
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        ));
    }


    private BudgetResponse mapToResponse(
            Budget budget) {

        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .categoryId(
                        budget.getCategory().getId()
                )
                .categoryName(
                        budget.getCategory().getName()
                )
                .categoryType(
                        budget.getCategory()
                                .getType()
                                .name()
                )
                .build();
    }


    @Override
    public BudgetAnalyticsResponse getBudgetAnalytics(
            Long budgetId,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(budgetId, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Budget not found"
                                ));


        LocalDate startDate =
                budget.getMonth()
                        .withDayOfMonth(1);


        LocalDate endDate =
                startDate.plusMonths(1);


        BigDecimal spentAmount =
                transactionRepository
                        .calculateCategorySpending(
                                user,
                                budget.getCategory(),
                                startDate,
                                endDate
                        );


        if (spentAmount == null) {
            spentAmount = BigDecimal.ZERO;
        }


        BigDecimal remainingAmount =
                budget.getAmount()
                        .subtract(spentAmount);


        BigDecimal utilizationPercentage =
                spentAmount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                budget.getAmount(),
                                2,
                                RoundingMode.HALF_UP
                        );


        boolean overBudget =
                spentAmount.compareTo(
                        budget.getAmount()
                ) > 0;


        return BudgetAnalyticsResponse.builder()
                .budgetId(budget.getId())
                .categoryName(
                        budget.getCategory().getName()
                )
                .month(budget.getMonth())
                .budgetAmount(budget.getAmount())
                .spentAmount(spentAmount)
                .remainingAmount(remainingAmount)
                .utilizationPercentage(
                        utilizationPercentage
                )
                .overBudget(overBudget)
                .build();
    }
}