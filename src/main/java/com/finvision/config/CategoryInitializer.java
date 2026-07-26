package com.finvision.config;

import com.finvision.category.entity.Category;
import com.finvision.category.entity.CategoryType;
import com.finvision.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {

        createCategory("Salary", CategoryType.INCOME);
        createCategory("Freelancing", CategoryType.INCOME);

        createCategory("Food", CategoryType.EXPENSE);
        createCategory("Travel", CategoryType.EXPENSE);
        createCategory("Shopping", CategoryType.EXPENSE);
        createCategory("Bills", CategoryType.EXPENSE);
        createCategory("Health", CategoryType.EXPENSE);
    }

    private void createCategory(String name, CategoryType type) {

        boolean exists = categoryRepository.findBySystemCategoryTrue()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));

        if (!exists) {
            categoryRepository.save(
                    Category.builder()
                            .name(name)
                            .type(type)
                            .systemCategory(true)
                            .build()
            );
        }
    }
}