package com.finvision.category.service.impl;

import com.finvision.category.dto.CategoryRequest;
import com.finvision.category.dto.CategoryResponse;
import com.finvision.category.entity.Category;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.category.service.CategoryService;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;


    @Override
    public CategoryResponse createCategory(
            CategoryRequest request,
            String email) {

        User user = getUser(email);

        Category category = new Category();

        category.setName(request.getName());
        category.setType(request.getType());
        category.setUser(user);
        category.setSystemCategory(false);

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }


    @Override
    public List<CategoryResponse> getAllCategories(
            String email) {

        User user = getUser(email);

        List<Category> systemCategories =
                categoryRepository.findBySystemCategoryTrue();

        List<Category> userCategories =
                categoryRepository.findByUser(user);

        return java.util.stream.Stream.concat(
                        systemCategories.stream(),
                        userCategories.stream()
                )
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(
            Long id,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        return mapToResponse(category);
    }


    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        category.setName(request.getName());
        category.setType(request.getType());

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }


    @Override
    public void deleteCategory(
            Long id,
            String email) {

        User user = getUser(email);

        Category category =
                categoryRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        categoryRepository.delete(category);
    }


    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }


    private CategoryResponse mapToResponse(
            Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}