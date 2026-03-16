package com.financas.projeto.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank
    String name,

    @NotBlank
    String email,

    @NotBlank
    String password
) {
}
