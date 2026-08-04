package com.finvision.budget.repository;

import com.finvision.budget.entity.Budget;
import com.finvision.category.entity.Category;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndCategoryAndMonth(
            User user,
            Category category,
            String month
    );
    Optional<Budget> findByIdAndUser(Long id, User user);
}