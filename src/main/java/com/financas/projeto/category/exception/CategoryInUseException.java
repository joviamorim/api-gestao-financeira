package com.financas.projeto.category.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException() {
        super("Category is being used by one or more transactions.");
    }
}
