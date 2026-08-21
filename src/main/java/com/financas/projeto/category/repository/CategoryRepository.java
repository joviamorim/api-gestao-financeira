package com.financas.projeto.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.financas.projeto.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findById(String categoryId);

    Optional<List<Category>> findAllByUserId(UUID userId);

    Optional<Category> findByNameAndUserId(String name, UUID userId);
}
