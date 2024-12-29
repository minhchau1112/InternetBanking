package com.example.backend.controller;

import com.example.backend.dto.response.LoginResponse;
import com.example.backend.model.*;
import com.example.backend.service.AccountService;
import com.example.backend.service.LoginService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        User createdUser = userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers());
    }

    @GetMapping("/api/auth/user")
    public ResponseEntity<LoginResponse.UserInformation> getCurrentUser() {
        String username = LoginService.getCurrentUserLogin().isPresent() ? LoginService.getCurrentUserLogin().get() : "";

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        LoginResponse.UserInformation userInformation = new LoginResponse.UserInformation();
        String role = getRole(user);
        userInformation.setRole(role);
        if (user!=null) {
            userInformation.setUsername(user.getUsername());
            if (role=="ROLE_CUSTOMER"){
                Account account = accountService.findByCustomerId(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Account not found"));
                userInformation.setAccountID(account.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userInformation);
    }
    public String getRole(User user) {
        if (user instanceof Admin) {
            return "ROLE_ADMIN";
        }
        if (user instanceof Customer) {
            return "ROLE_CUSTOMER";
        }
        if (user instanceof Employee) {
            return "ROLE_EMPLOYEE";
        }
        throw new IllegalStateException("Unknown user type: " + user.getClass().getName());
    }

}
