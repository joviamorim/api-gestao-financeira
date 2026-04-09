package com.financas.projeto.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());
    }
}
