package com.finvision.category.controller;

import com.finvision.category.dto.CategoryRequest;
import com.finvision.category.dto.CategoryResponse;
import com.finvision.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {

        CategoryResponse response =
                categoryService.createCategory(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            Authentication authentication) {

        return ResponseEntity.ok(
                categoryService.getAllCategories(
                        authentication.getName()
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(
                        id,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {

        categoryService.deleteCategory(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}