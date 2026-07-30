package com.finvision.transaction.repository;

import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.entity.Transaction;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);
    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUserAndType(User user, CategoryType type);

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);
}
