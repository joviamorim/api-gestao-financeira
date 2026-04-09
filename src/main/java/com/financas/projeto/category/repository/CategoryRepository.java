package com.financas.projeto.category.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.financas.projeto.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findById(String categoryId);
}
