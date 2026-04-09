package com.financas.projeto.auth.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Email already in use");
    }

}
