package com.financas.projeto.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.dto.CategoryResponse;
import com.financas.projeto.category.dto.DeleteCategoryRequest;
import com.financas.projeto.category.dto.RegisterCategoryRequest;
import com.financas.projeto.category.dto.UpdateCategoryRequest;
import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryAlreadyExistsException;
import com.financas.projeto.category.exception.CategoryInUseException;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.exception.CategoryUnauthorizedException;
import com.financas.projeto.category.mapper.CategoryMapper;
import com.financas.projeto.category.repository.CategoryRepository;
import com.financas.projeto.transaction.repository.TransactionRepository;
import com.financas.projeto.user.entity.User;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TransactionRepository transactionRepository;

    CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
            TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.transactionRepository = transactionRepository;
    }

    public CategoryListResponse getAllCategories(UUID userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId)
                .orElseThrow(() -> new CategoryNotFoundException());

        return categoryMapper.toListResponse(categories);
    }

    public Category findCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());
    }

    public CategoryResponse createCategory(
            User user,
            RegisterCategoryRequest request) {
        boolean categoryAlreadyExists = categoryRepository.findByNameAndUserId(request.name(), user.getId())
                .isPresent();
        if (categoryAlreadyExists) {
            throw new CategoryAlreadyExistsException();
        }

        Category category = new Category();
        category.setUser(user);
        category.setName(request.name());
        Category result = categoryRepository.save(category);

        return categoryMapper.toResponse(result);
    }

    public CategoryResponse updateCategory(UpdateCategoryRequest request, User user) {
        Category category = categoryRepository.findById(request.id())
                .orElseThrow(() -> new CategoryNotFoundException());

        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryUnauthorizedException();
        }

        category.setName(request.name());

        categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    public CategoryResponse deleteCategory(DeleteCategoryRequest request, User user) {
        if (transactionRepository.existsByUserIdAndCategoryId(user.getId(), request.id())) {
            throw new CategoryInUseException();
        }

        Category category = categoryRepository.findById(request.id())
                .orElseThrow(() -> new CategoryNotFoundException());

        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryUnauthorizedException();
        }

        categoryRepository.delete(category);
        return categoryMapper.toResponse(category);
    }
}
