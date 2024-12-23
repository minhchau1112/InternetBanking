package com.example.backend.service;

import com.example.backend.enums.UserRole;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername((username));
    }

    public User handleCreateUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists.");
        }
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> fetchAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}