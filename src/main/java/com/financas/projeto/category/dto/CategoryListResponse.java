package com.financas.projeto.category.dto;

import java.util.List;

public record CategoryListResponse(
        List<CategoryResponse> categories) {
}
