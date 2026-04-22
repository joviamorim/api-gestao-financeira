package com.financas.projeto.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financas.projeto.category.dto.CategoryListResponse;
import com.financas.projeto.category.dto.CategoryResponse;
import com.financas.projeto.category.entity.Category;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.category.mapper.CategoryMapper;
import com.financas.projeto.category.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private CategoryMapper categoryMapper;

        @InjectMocks
        private CategoryService categoryService;

        @Test
        void shouldGetAllCategoriesSuccessfully() {
                // Arrange
                UUID categoryId = UUID.randomUUID();
                String categoryName = "Test Category";

                Category category = new Category(categoryId);
                category.setName(categoryName);

                List<Category> categories = List.of(category);

                CategoryListResponse categoryListResponse = new CategoryListResponse(
                                List.of(new CategoryResponse(categoryId, categoryName)));

                when(categoryRepository.findAll())
                                .thenReturn(categories);

                when(categoryMapper.toListResponse(categories))
                                .thenReturn(categoryListResponse);

                // Act

                CategoryListResponse response = categoryService.getAllCategories();

                // Assert
                assertNotNull(response);
                assertEquals(categoryListResponse, response);
                assertEquals(1, response.categories().size());

                verify(categoryRepository).findAll();
                verify(categoryMapper).toListResponse(categories);
                verifyNoMoreInteractions(categoryMapper);
        }

        @Test
        void shouldReturnEmptyListWhenNoCategoriesFound() {
                // Arrange
                List<Category> categories = List.of();

                CategoryListResponse categoryListResponse = new CategoryListResponse(List.of());

                when(categoryRepository.findAll())
                                .thenReturn(categories);

                when(categoryMapper.toListResponse(categories))
                                .thenReturn(categoryListResponse);

                // Act
                CategoryListResponse response = categoryService.getAllCategories();

                // Assert
                assertNotNull(response);
                assertTrue(response.categories().isEmpty());
                assertEquals(categoryListResponse, response);

                verify(categoryRepository).findAll();
                verify(categoryMapper).toListResponse(categories);
                verifyNoMoreInteractions(categoryMapper);
        }

        @Test
        void shouldFindCategoryByIdSuccessfully() {
                // Arrange
                UUID categoryId = UUID.randomUUID();
                String categoryName = "Test Category";

                Category category = new Category(categoryId);
                category.setName(categoryName);

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.of(category));

                // Act
                Category response = categoryService.findCategoryById(categoryId);

                // Assert
                assertNotNull(response);
                assertSame(category, response);

                verify(categoryRepository).findById(categoryId);
        }

        @Test
        void shouldThrowWhenCategoryNotFound() {
                // Arrange
                UUID categoryId = UUID.randomUUID();

                when(categoryRepository.findById(categoryId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(CategoryNotFoundException.class,
                                () -> categoryService.findCategoryById(categoryId));

                verify(categoryRepository).findById(categoryId);
        }
}
