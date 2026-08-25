package com.finvision.budget.repository;

import com.finvision.budget.entity.Budget;
import com.finvision.category.entity.Category;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;


import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    long countByUser(User user);

    List<Budget> findByUser(User user);

    Optional<Budget> findByIdAndUser(
            Long id,
            User user
    );

    Optional<Budget> findByUserAndCategoryAndMonth(
            User user,
            Category category,
            LocalDate month
    );


    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user
        AND t.category = :category
        AND t.transactionDate >= :startDate
        AND t.transactionDate < :endDate
        """)
    BigDecimal calculateCategorySpending(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}