package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final LoginService loginService;

    public AuthenticationController(AuthenticationManagerBuilder authenticationManagerBuilder, LoginService securityService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.loginService = securityService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String access_token = loginService.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setAccessToken(access_token);
        loginResponse.setRole(user.getAuthorities().iterator().next().getAuthority());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setRefreshToken(null);
        loginResponse.setExpiresIn(loginService.getJwtExpiration());
        loginResponse.setRefeshExpiresIn(loginService.getRefreshExpiresIn());
        loginResponse.setTokenType("Bearer");

        return ResponseEntity.ok().body(loginResponse);
    }
}