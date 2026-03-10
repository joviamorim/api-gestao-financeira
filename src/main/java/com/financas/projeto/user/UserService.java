package com.financas.projeto.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password) {
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}
