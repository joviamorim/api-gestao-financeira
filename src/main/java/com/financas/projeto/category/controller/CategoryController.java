package com.financas.projeto.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.dto.CategoryResponse;
import com.financas.projeto.category.dto.DeleteCategoryRequest;
import com.financas.projeto.category.dto.RegisterCategoryRequest;
import com.financas.projeto.category.dto.UpdateCategoryRequest;
import com.financas.projeto.category.service.CategoryService;
import com.financas.projeto.common.response.ApiResponse;
import com.financas.projeto.user.entity.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getAllCategories(
            @AuthenticationPrincipal User user) {
        CategoryListResponse categories = categoryService.getAllCategories(user.getId());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid RegisterCategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(user, request);
        return ResponseEntity.ok(ApiResponse.success(category, "Category created successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateCategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(request, user);

        return ResponseEntity.ok(ApiResponse.success(category, "Category updated successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid DeleteCategoryRequest request) {
        CategoryResponse category = categoryService.deleteCategory(request, user);

        return ResponseEntity.ok(ApiResponse.success(category, "Category deleted successfully"));
    }
}
