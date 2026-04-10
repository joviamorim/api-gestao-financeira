package com.financas.projeto.auth.exception;

public class InvalidAuthorizationHeaderException extends RuntimeException {
    public InvalidAuthorizationHeaderException() {
        super("Invalid Authorization header");
    }

}
