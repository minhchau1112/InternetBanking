package com.example.backend.config;

import com.example.backend.model.Admin;
import com.example.backend.model.Customer;
import com.example.backend.model.Employee;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailsServiceConfiguration implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.handleGetUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(getRole(user)))
        );
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
        return null;
    }
}
