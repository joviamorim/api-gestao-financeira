package com.financas.projeto.unit.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.financas.projeto.auth.dto.AuthResponse;
import com.financas.projeto.auth.dto.LoginRequest;
import com.financas.projeto.auth.dto.RegisterRequest;
import com.financas.projeto.auth.exception.InvalidCredentialsException;
import com.financas.projeto.auth.exception.UserAlreadyExistsException;
import com.financas.projeto.auth.service.AuthService;
import com.financas.projeto.auth.service.JwtService;
import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

        @Mock
        private JwtService jwtService;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private AuthService authService;

        @Test
        void shouldRegisterSuccessfully() {
                // Arrange
                String name = "John Doe";
                String email = "test@test.com";
                String password = "password123";

                RegisterRequest request = new RegisterRequest(name, email, password);

                String encryptedPassword = "encoded-password";
                String token = "jwt-token";

                when(userRepository.existsByEmail(email))
                                .thenReturn(false);

                when(passwordEncoder.encode(password))
                                .thenReturn(encryptedPassword);

                when(jwtService.generateToken(any(User.class)))
                                .thenReturn(token);

                // Act
                AuthResponse result = authService.register(request);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

                verify(userRepository).save(userCaptor.capture());

                User savedUser = userCaptor.getValue();

                assertNotNull(result);
                assertSame(email, savedUser.getEmail());
                assertSame(name, savedUser.getName());
                assertSame(encryptedPassword, savedUser.getPassword());
                assertSame(token, result.token());

                verify(userRepository).existsByEmail(email);
                verify(passwordEncoder).encode(password);
                verify(userRepository).save(any(User.class));
                verify(jwtService).generateToken(any(User.class));
        }

        @Test
        void shouldThrowWhenEmailAlreadyExists() {
                // Arrange
                String name = "John Doe";
                String email = "test@test.com";
                String password = "password123";

                when(userRepository.existsByEmail(email))
                                .thenReturn(true);

                RegisterRequest request = new RegisterRequest(name, email, password);

                // Act & Assert
                assertThrows(UserAlreadyExistsException.class,
                                () -> authService.register(request));

                verify(userRepository).existsByEmail(email);
                verify(userRepository, never()).save(any(User.class));
                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(jwtService);
        }

        @Test
        void shouldLoginSuccessfully() {
                // Arrange
                String email = "test@test.com";
                String password = "password123";

                LoginRequest request = new LoginRequest(email, password);

                String encryptedPassword = "encoded-password";
                String token = "jwt-token";

                User user = new User();
                user.setEmail(email);
                user.setPassword(encryptedPassword);

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(password, encryptedPassword))
                                .thenReturn(true);

                when(jwtService.generateToken(user))
                                .thenReturn(token);

                // Act
                AuthResponse result = authService.login(request);

                // Assert
                assertNotNull(result);
                assertEquals(token, result.token());

                verify(userRepository).findByEmail(email);
                verify(passwordEncoder).matches(password, encryptedPassword);
                verify(jwtService).generateToken(user);
        }

        @Test
        void shouldThrowWhenInvalidEmail() {
                // Arrange
                String email = "test@test.com";
                String password = "password123";

                LoginRequest request = new LoginRequest(email, password);

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(InvalidCredentialsException.class,
                                () -> authService.login(request));

                verify(userRepository).findByEmail(email);
                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(jwtService);
        }

        @Test
        void shouldThrowWhenInvalidPassword() {
                // Arrange
                String email = "test@test.com";
                String password = "password123";

                LoginRequest request = new LoginRequest(email, password);

                String encryptedPassword = "encoded-password";

                User user = new User();
                user.setEmail(email);
                user.setPassword(encryptedPassword);

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(password, encryptedPassword))
                                .thenReturn(false);

                // Act & Assert
                assertThrows(InvalidCredentialsException.class,
                                () -> authService.login(request));

                verify(userRepository).findByEmail(email);
                verify(passwordEncoder).matches(password, encryptedPassword);
                verifyNoInteractions(jwtService);
        }

}