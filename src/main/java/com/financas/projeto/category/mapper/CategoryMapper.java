package com.financas.projeto.category.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.dto.CategoryResponse;
import com.financas.projeto.category.entity.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public CategoryListResponse toListResponse(List<Category> categories) {
        return new CategoryListResponse(categories.stream().map(this::toResponse).toList());
    }
}
