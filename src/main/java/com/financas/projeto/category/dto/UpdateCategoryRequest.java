package com.financas.projeto.category.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
                @NotNull UUID id,

                @NotNull String name) {

}
