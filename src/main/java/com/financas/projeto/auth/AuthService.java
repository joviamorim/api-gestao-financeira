package com.financas.projeto.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.financas.projeto.auth.dto.AuthResponse;
import com.financas.projeto.auth.dto.LoginRequest;
import com.financas.projeto.auth.dto.RegisterRequest;
import com.financas.projeto.user.User;
import com.financas.projeto.user.UserService;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = userService.createUser(
            request.name(),
            request.email(),
            encryptedPassword
        );

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
