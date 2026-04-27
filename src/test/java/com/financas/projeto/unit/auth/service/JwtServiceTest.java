package com.financas.projeto.unit.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.financas.projeto.auth.exception.InvalidAuthorizationHeaderException;
import com.financas.projeto.auth.exception.InvalidJwtTokenException;
import com.financas.projeto.auth.service.JwtService;
import com.financas.projeto.user.entity.User;

public class JwtServiceTest {

    @Test
    void shouldGenerateValidToken() {
        // Arrange
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");

        User user = new User();
        user.setEmail("test@test.com");

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractEmailFromValidToken() {
        // Arrange
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");

        User user = new User();
        user.setEmail("test@test.com");

        String token = jwtService.generateToken(user);
        String header = "Bearer " + token;

        // Act
        String email = jwtService.extractEmailFromAuthHeader(header);

        // Assert
        assertEquals("test@test.com", email);
    }

    @Test
    void shouldThrowWhenHeaderIsInvalid() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");

        String invalidHeader = "InvalidHeader";

        assertThrows(InvalidAuthorizationHeaderException.class,
                () -> jwtService.extractEmailFromAuthHeader(invalidHeader));
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");

        String header = "Bearer token_invalido";

        assertThrows(InvalidJwtTokenException.class,
                () -> jwtService.extractEmailFromAuthHeader(header));
    }

    @Test
    void shouldThrowWhenTokenSignedWithDifferentSecret() {
        // Arrange
        JwtService jwtService1 = new JwtService();
        JwtService jwtService2 = new JwtService();

        ReflectionTestUtils.setField(jwtService1, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService2, "secret", "DIFFERENT_SECRET_12345678901234567890");

        User user = new User();
        user.setEmail("test@test.com");

        String token = jwtService1.generateToken(user);
        String header = "Bearer " + token;

        // Act & Assert
        assertThrows(InvalidJwtTokenException.class,
                () -> jwtService2.extractEmailFromAuthHeader(header));
    }
}
