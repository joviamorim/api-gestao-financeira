package com.financas.projeto.category.dto;

import jakarta.validation.constraints.NotNull;

public record RegisterCategoryRequest(
                @NotNull String name) {

}
