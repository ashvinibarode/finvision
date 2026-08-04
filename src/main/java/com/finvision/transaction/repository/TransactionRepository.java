package com.finvision.transaction.repository;

import com.finvision.category.entity.Category;

import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.entity.Transaction;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserAndType(User user, CategoryType type);

    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );


    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title
    );
    @Query("""
SELECT COALESCE(SUM(t.amount), 0)
FROM Transaction t
WHERE t.user = :user
AND t.category = :category
AND t.type = :type
AND FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') = :month
""")
    BigDecimal getMonthlyExpense(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("type") CategoryType type,
            @Param("month") String month
    );

}