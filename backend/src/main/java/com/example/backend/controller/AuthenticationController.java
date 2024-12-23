package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.model.Account;
import com.example.backend.model.User;
import com.example.backend.service.AccountService;
import com.example.backend.service.LoginService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AccountService accountService;

    private final UserService userService;

    private final LoginService loginService;

    public AuthenticationController(AuthenticationManagerBuilder authenticationManagerBuilder, LoginService securityService, AccountService accountService, UserService userService, LoginService loginService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.loginService = securityService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> getRefreshToken(@CookieValue(name="refresh_token") String refresh_token) {
        return ResponseEntity.ok().body(refresh_token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String access_token = loginService.createAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setAccessToken(access_token);
        loginResponse.setRole(authentication.getAuthorities().iterator().next().getAuthority());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setExpiresIn(loginService.getAccessTokenExpiration());
        loginResponse.setTokenType("Bearer");

        if ("ROLE_CUSTOMER".equals(loginResponse.getRole())) {
            Account account = accountService.findByCustomerId(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found"));
            loginResponse.setAccountID(account.getId());
        } else {
            loginResponse.setAccountID(null);
        }
        String refresh_token = loginService.createRefreshToken(request.getUsername(),loginResponse);
        return ResponseEntity.ok().body(loginResponse);
    }

}