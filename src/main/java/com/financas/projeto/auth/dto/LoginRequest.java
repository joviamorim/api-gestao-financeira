package com.financas.projeto.auth.dto;

public record LoginRequest(
    String email,
    String password
) {
}
