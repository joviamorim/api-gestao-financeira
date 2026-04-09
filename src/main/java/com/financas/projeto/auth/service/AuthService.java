package com.financas.projeto.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.financas.projeto.auth.dto.AuthResponse;
import com.financas.projeto.auth.dto.LoginRequest;
import com.financas.projeto.auth.dto.RegisterRequest;
import com.financas.projeto.auth.exception.InvalidCredentialsException;
import com.financas.projeto.auth.exception.UserAlreadyExistsException;
import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.repository.UserRepository;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException();
        }

        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encryptedPassword);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
