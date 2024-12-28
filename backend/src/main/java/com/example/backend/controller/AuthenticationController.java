package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.EmailVerifyResponse;
import com.example.backend.dto.response.LoginResponse;
import com.example.backend.dto.response.RestResponse;
import com.example.backend.dto.response.VerifyOTPResponse;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.InvalidException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.model.*;
import com.example.backend.service.*;
import com.example.backend.utils.annotation.APIMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AccountService accountService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final LoginService loginService;

    private final CustomerService customerService;

    private final EmailService emailService;

    private final Random random = new Random();

    private final OTPService otpService;

    public AuthenticationController(RefreshTokenService refreshTokenService, AuthenticationManagerBuilder authenticationManagerBuilder, LoginService securityService, AccountService accountService, UserService userService, LoginService loginService, CustomerService customerService, EmailService emailService, OTPService otpService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.loginService = securityService;
        this.accountService = accountService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.customerService = customerService;
        this.emailService = emailService;
        this.otpService = otpService;
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
                .secure(false)
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
                .secure(false)
                .sameSite("None")
                .maxAge(loginService.getRefreshExpiresIn())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/logout")
    @APIMessage("Logout User")
    public ResponseEntity<Void> logout() throws InvalidException {

        String username = LoginService.getCurrentUserLogin().isPresent()?LoginService.getCurrentUserLogin().get():null;

        if (username == null) {
            throw new InvalidException("Access Token is not valid");
        }

        refreshTokenService.deleteRefreshToken("refresh_token:" + username);

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(null);

    }

    @PostMapping("/verify-email")
    @APIMessage("Customer is found")
    public ResponseEntity<EmailVerifyResponse> findCustomerByEmail(@RequestBody Map<String, String> requestBody) throws EmailNotFoundException {
        String email = requestBody.get("email");
        return customerService.findByEmail(email)
                .map(customer -> {
                    EmailVerifyResponse emailVerifyResponse = new EmailVerifyResponse(
                            customer.getId(),
                            customer.getUsername(),
                            customer.getName(),
                            customer.getEmail(),
                            customer.getPhone()
                    );
                    return ResponseEntity.ok(emailVerifyResponse);
                })
                .orElseThrow(() -> new EmailNotFoundException("NOT_FOUND_EMAIL"));
    }

    @PostMapping("/forgot-password")
    @APIMessage("Handle forgot password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        RestResponse<?> res = new RestResponse<>();

        String email = request.get("email");
        if (email == null || email.isBlank()) {
            res.setStatus(400);
            res.setMessage("Invalid email");
            res.setError("INVALID_EMAIL");
            res.setData(null);
            return ResponseEntity.badRequest().body(res);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpService.saveOtp(email, otp, 1);

        Map<String, Object> variables = new HashMap<>();
        variables.put("OTP_CODE", otp);
        emailService.sendEmailFromTemplateSync(email, "Reset Your Password", "verify-email", variables);

        res.setStatus(200);
        res.setMessage("Sent OTP code successfully");
        res.setError(null);
        res.setData(null);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/verify-reset-otp")
    @APIMessage("Verify reset OTP")
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> request) throws EmailNotFoundException, OTPNotFoundException {
        RestResponse<?> res = new RestResponse<>();

        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.isBlank()) {
            throw new EmailNotFoundException("Email can not be blank");
        }

        if (otp == null || otp.isBlank()) {
            throw new OTPNotFoundException("OTP can not be blank");
        }

        String storedOtp = otpService.getOtp(email);
        if (storedOtp == null) {
            throw new OTPNotFoundException("OTP is not valid");
        }

        if (!storedOtp.equals(otp)) {
            throw new OTPNotFoundException("OTP is not valid");
        }

        otpService.deleteOtp(email);


        return ResponseEntity.ok().body(new VerifyOTPResponse(storedOtp));
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