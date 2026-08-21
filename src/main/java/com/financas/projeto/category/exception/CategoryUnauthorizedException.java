package com.financas.projeto.category.exception;

public class CategoryUnauthorizedException extends RuntimeException {
    public CategoryUnauthorizedException() {
        super("You are not authorized to access this category.");
    }
}
