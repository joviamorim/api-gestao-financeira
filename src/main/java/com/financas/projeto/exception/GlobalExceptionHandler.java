package com.financas.projeto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financas.projeto.auth.exception.InvalidAuthorizationHeaderException;
import com.financas.projeto.auth.exception.InvalidCredentialsException;
import com.financas.projeto.auth.exception.InvalidJwtTokenException;
import com.financas.projeto.auth.exception.UserAlreadyExistsException;
import com.financas.projeto.balance.exception.BalanceStartDateIsAfterException;
import com.financas.projeto.category.exception.CategoryNotFoundException;
import com.financas.projeto.common.response.ApiError;
import com.financas.projeto.transaction.exception.TransactionNotFoundException;
import com.financas.projeto.transaction.exception.TransactionUnauthorizedException;
import com.financas.projeto.user.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ApiError> handleInvalidAuthorizationHeader(InvalidAuthorizationHeaderException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ApiError> handleInvalidJwtToken(InvalidJwtTokenException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiError> handleTransactionNotFound(TransactionNotFoundException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(TransactionUnauthorizedException.class)
    public ResponseEntity<ApiError> handleTransactionUnauthorized(TransactionUnauthorizedException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.FORBIDDEN.value());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFound(CategoryNotFoundException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(BalanceStartDateIsAfterException.class)
    public ResponseEntity<ApiError> handleBalanceStartDateIsAfter(BalanceStartDateIsAfterException ex) {
        ApiError errorResponse = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        ApiError errorResponse = new ApiError(
                "Internal server error: ",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
