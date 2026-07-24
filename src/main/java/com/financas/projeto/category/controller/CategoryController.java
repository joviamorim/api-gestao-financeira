package com.financas.projeto.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.service.CategoryService;
import com.financas.projeto.common.response.ApiResponse;
import com.financas.projeto.user.entity.User;

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
}
