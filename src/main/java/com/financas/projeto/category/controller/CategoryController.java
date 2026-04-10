package com.financas.projeto.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.service.CategoryService;
import com.financas.projeto.common.response.ApiResponse;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getAllCategories() {
        CategoryListResponse categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
