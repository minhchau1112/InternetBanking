package com.example.backend.repository;

import com.example.backend.model.Customer;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsById(Integer id);

    boolean existsByUsername(String username);
}
