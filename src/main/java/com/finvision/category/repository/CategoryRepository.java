package com.finvision.category.repository;

import com.finvision.category.entity.Category;
import com.finvision.category.entity.CategoryType;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    List<Category> findByType(CategoryType type);

    Optional<Category> findByIdAndUser(
            Long id,
            User user
    );

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.id = :id
            AND (
                c.user = :user
                OR c.systemCategory = true
            )
            """)
    Optional<Category> findAccessibleCategory(
            @Param("id") Long id,
            @Param("user") User user
    );

    List<Category> findBySystemCategoryTrue();
}