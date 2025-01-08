package com.example.backend.controller;

import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.InvalidException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.exception.RecaptchaException;
import com.example.backend.model.*;
import com.example.backend.service.*;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Value("${recaptcha.secretKey}")
    private String secretKey;

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
    @Operation(
            summary = "Refresh Access Token",
            description = "Refreshes the access token using a valid refresh token."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Refresh token successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Refresh token successful\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"user\": {\n" +
                                    "      \"userID\": 1,\n" +
                                    "      \"username\": \"customer\",\n" +
                                    "      \"accountID\": 1,\n" +
                                    "      \"role\": \"ROLE_CUSTOMER\"\n" +
                                    "    },\n" +
                                    "    \"accessToken\": \"access_token\",\n" +
                                    "    \"expiresIn\": 3600,\n" +
                                    "    \"tokenType\": \"Bearer\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired refresh token",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"error\": \"INVALID_TOKEN\",\n" +
                                    "  \"message\": \"Invalid or expired temporary token\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
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
    @Operation(
            summary = "Login user",
            description = "Authenticate the user and return an access token along with user details."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Login successful\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"user\": {\n" +
                                    "      \"userID\": 1,\n" +
                                    "      \"username\": \"john_doe\",\n" +
                                    "      \"accountID\": 123,\n" +
                                    "      \"role\": \"ROLE_CUSTOMER\"\n" +
                                    "    },\n" +
                                    "    \"accessToken\": \"access_token_value\",\n" +
                                    "    \"expiresIn\": 3600,\n" +
                                    "    \"tokenType\": \"Bearer\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input or reCAPTCHA validation failed",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"error\": \"INVALID_RECAPTCHA\",\n" +
                                    "  \"message\": \"Invalid reCAPTCHA response\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized, invalid credentials",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 401,\n" +
                                    "  \"error\": \"INVALID_CREDENTIALS\",\n" +
                                    "  \"message\": \"User not found or incorrect credentials\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,@RequestParam String recaptchaResponse)  {

        boolean isRecaptchaValid = verifyRecaptcha(recaptchaResponse);
        if (!isRecaptchaValid) {
            throw new RecaptchaException("Invalid reCAPTCHA response");
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse loginResponse = new LoginResponse();

        LoginResponse.UserInformation userInformation = new LoginResponse.UserInformation();

        userInformation.setRole(authentication.getAuthorities().iterator().next().getAuthority());
        userInformation.setUserID(user.getId());
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
                .httpOnly(false)
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
    @Operation(
            summary = "Logout user",
            description = "Logs out the user by invalidating the refresh token and clearing the cookie."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Logout User\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Full authentication is required to access this resource",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 401,\n" +
                                    "  \"error\": \"Full authentication is required to access this resource\",\n" +
                                    "  \"message\": \"Xác thực không hợp lệ !\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<Void> logout() throws InvalidException {

        String username = LoginService.getCurrentUserLogin().isPresent()?LoginService.getCurrentUserLogin().get():null;

        if (username == null) {
            throw new InvalidException("Access Token is not valid");
        }

        System.out.println(username);

        refreshTokenService.deleteRefreshToken(username);

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(false)
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
    @Operation(
            summary = "Find customer by email",
            description = "Finds a customer using the provided email address."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Customer found successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Customer is found\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"id\": 1,\n" +
                                    "    \"username\": \"john_doe\",\n" +
                                    "    \"name\": \"John Doe\",\n" +
                                    "    \"email\": \"john.doe@example.com\",\n" +
                                    "    \"phone\": \"1234567890\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Email not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"NOT_FOUND_EMAIL\",\n" +
                                    "  \"message\": \"The email was not found\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<EmailVerifyResponse> findCustomerByEmail(@RequestBody VerifyEmailRequest request) throws EmailNotFoundException {
        String email = request.getEmail();
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
    @Operation(
            summary = "Handle forgot password",
            description = "Sends OTP to the user's email for password reset."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OTP sent successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Sent OTP code successfully\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid email",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"error\": \"INVALID_EMAIL\",\n" +
                                    "  \"message\": \"Invalid email\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        RestResponse<?> res = new RestResponse<>();

        String email = request.getEmail();
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
    @Operation(
            summary = "Verify reset OTP",
            description = "Verifies the OTP sent to the user's email for password reset"
    )
    @ApiResponse(
            responseCode = "200",
            description = "OTP verified successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"OTP verified successfully\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"temporary_token\": \"393297\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Invalid input or OTP",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"OTP is not valid\",\n" +
                                    "  \"message\": \"The OTP was not found\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<?> verifyResetOtp(@RequestBody VerifyResetOtpRequest request) throws EmailNotFoundException, OTPNotFoundException {

        String email = request.getEmail();
        String otp = request.getOtp();

        if (email == null || email.isBlank()) {
            throw new EmailNotFoundException("Email can not be blank");
        }

        if (otp == null || otp.isBlank()) {
            throw new OTPNotFoundException("OTP can not be blank");
        }

        String storedOtp = otpService.getOtp(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new OTPNotFoundException("OTP is not valid");
        }

        otpService.deleteOtp(email);

        return ResponseEntity.ok().body(new VerifyOTPResponse(storedOtp));
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the password for the customer using the provided email and new password."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reset password success",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Reset password success.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"message\": \"Reset password success.\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired temporary token",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"statusCode\": 400,\n" +
                                    "  \"message\": \"Invalid or expired temporary token\",\n" +
                                    "  \"data\": null,\n" +
                                    "  \"error\": \"INVALID_TOKEN\"\n" +
                                    "}"
                    )
            )
    )
    @PostMapping("/reset-password")
    @APIMessage("Reset password success.")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) throws EmailNotFoundException {
        customerService.resetPassword(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new ResetPasswordResponse());
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

    private boolean verifyRecaptcha(String recaptchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        String params = "secret=" + secretKey + "&response=" + recaptchaResponse;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String body = response.getBody();
            return body.contains("\"success\": true");
        }

        return false;
    }

}