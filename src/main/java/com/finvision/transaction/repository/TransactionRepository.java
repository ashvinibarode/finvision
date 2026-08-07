package com.finvision.transaction.repository;

import com.finvision.category.entity.Category;
import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.entity.Transaction;

import com.finvision.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    long countByUser(User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUser(User user);

    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> findByUserAndCategory_Type(
            User user,
            CategoryType type
    );

    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Transaction> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title
    );

    List<Transaction> findTop5ByUserOrderByTransactionDateDesc(User user);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.user = :user
      AND t.category = :category
      AND t.category.type = :type
      AND FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') = :month
""")
    BigDecimal getMonthlyExpense(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("type") CategoryType type,
            @Param("month") String month
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.user = :user
     AND t.category.type = :type
""")
    BigDecimal getTotalIncome(
            @Param("user") User user,
            @Param("type") CategoryType type
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.user = :user
     AND t.category.type = :type
""")
    BigDecimal getTotalExpense(
            @Param("user") User user,
            @Param("type") CategoryType type
    );

    @Query("""
SELECT c.name, SUM(t.amount)
FROM Transaction t
JOIN t.category c
WHERE t.user = :user
AND t.category.type = :type
GROUP BY c.name
ORDER BY SUM(t.amount) DESC
""")
    List<Object[]> getTopExpenseCategories(
            @Param("user") User user,
            @Param("type") CategoryType type
    );


}