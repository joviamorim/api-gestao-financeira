package com.financas.projeto.category.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record DeleteCategoryRequest(
                @NotNull UUID id) {
}
