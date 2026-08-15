package com.finvision.category.service;

import com.finvision.category.dto.CategoryRequest;
import com.finvision.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(
            CategoryRequest request,
            String email
    );

    List<CategoryResponse> getAllCategories(
            String email
    );

    CategoryResponse getCategoryById(
            Long id,
            String email
    );

    CategoryResponse updateCategory(
            Long id,
            CategoryRequest request,
            String email
    );

    void deleteCategory(
            Long id,
            String email
    );
}
