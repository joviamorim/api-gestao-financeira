package com.financas.projeto.user.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.exception.UserNotFoundException;
import com.financas.projeto.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldFindByEmailSuccessfully() {
        // Arrange
        String email = "test@test.com";
        User user = new User();
        user.setEmail(email);
        user.setName("test");
        user.setPassword("password");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertSame(user, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        String email = "test@test.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));

        verify(userRepository).findByEmail(email);
    }
}
