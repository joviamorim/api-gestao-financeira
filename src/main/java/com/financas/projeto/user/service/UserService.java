package com.financas.projeto.user.service;

import org.springframework.stereotype.Service;

import com.financas.projeto.user.entity.User;
import com.financas.projeto.user.exception.UserNotFoundException;
import com.financas.projeto.user.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());
    }
}
