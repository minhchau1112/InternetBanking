package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.exception.InvalidException;
import com.example.backend.model.*;
import com.example.backend.service.AccountService;
import com.example.backend.service.LoginService;
import com.example.backend.service.RefreshTokenService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AccountService accountService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final LoginService loginService;

    public AuthenticationController(RefreshTokenService refreshTokenService, AuthenticationManagerBuilder authenticationManagerBuilder, LoginService securityService, AccountService accountService, UserService userService, LoginService loginService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.loginService = securityService;
        this.accountService = accountService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/refresh")
    public ResponseEntity<LoginResponse> getRefreshToken(@CookieValue(name="refresh_token") String refreshToken) throws InvalidException {
        Jwt decodedToken = loginService.checkValidRefreshToken(refreshToken);
        String username = decodedToken.getSubject();

        if (refreshToken==null){
            throw new InvalidException("Refresh Token không hợp lệ");
        }

        // Check in redis by token and username
        String storedToken = refreshTokenService.getRefreshToken(username);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new InvalidException("Refresh Token không hợp lệ");
        }

        // Create new refresh_token

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse loginResponse = new LoginResponse();

        LoginResponse.UserInformation userInformation = new LoginResponse.UserInformation();

        userInformation.setRole(getRole(user));
        userInformation.setUsername(user.getUsername());
        if ("ROLE_CUSTOMER".equals(userInformation.getRole())) {
            Account account = accountService.findByCustomerId(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found"));
            userInformation.setAccountID(account.getId());
        } else {
            userInformation.setAccountID(null);
        }

        loginResponse.setUser(userInformation);
        loginResponse.setExpiresIn(loginService.getAccessTokenExpiration());
        loginResponse.setTokenType("Bearer");

        String access_token = loginService.createAccessToken(username,loginResponse);
        loginResponse.setAccessToken(access_token);

        String refresh_token = loginService.createRefreshToken(username,loginResponse);
        refreshTokenService.storeRefreshToken(username, refresh_token, loginService.getRefreshExpiresIn());
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token",refresh_token)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .maxAge(loginService.getRefreshExpiresIn())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse loginResponse = new LoginResponse();

        LoginResponse.UserInformation userInformation = new LoginResponse.UserInformation();

        userInformation.setRole(authentication.getAuthorities().iterator().next().getAuthority());
        userInformation.setUsername(user.getUsername());
        if ("ROLE_CUSTOMER".equals(userInformation.getRole())) {
            Account account = accountService.findByCustomerId(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found"));
            userInformation.setAccountID(account.getId());
        } else {
            userInformation.setAccountID(null);
        }

        loginResponse.setUser(userInformation);
        loginResponse.setExpiresIn(loginService.getAccessTokenExpiration());
        loginResponse.setTokenType("Bearer");

        String access_token = loginService.createAccessToken(user.getUsername(),loginResponse);
        loginResponse.setAccessToken(access_token);

        String refresh_token = loginService.createRefreshToken(request.getUsername(),loginResponse);
        refreshTokenService.storeRefreshToken(request.getUsername(), refresh_token, loginService.getRefreshExpiresIn());
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token",refresh_token)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .maxAge(loginService.getRefreshExpiresIn())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
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