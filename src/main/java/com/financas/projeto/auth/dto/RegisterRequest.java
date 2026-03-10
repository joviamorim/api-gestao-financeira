package com.financas.projeto.auth.dto;

public record RegisterRequest(
    String name,
    String email,
    String password
) {
}
