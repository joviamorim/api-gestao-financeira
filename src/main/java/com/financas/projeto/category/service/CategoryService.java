package com.financas.projeto.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.mapper.CategoryMapper;
import com.financas.projeto.category.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryListResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categoryMapper.toListResponse(categories);
    }

    public Category findCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());
    }
}
