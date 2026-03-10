package com.financas.projeto.category;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
    }
}
