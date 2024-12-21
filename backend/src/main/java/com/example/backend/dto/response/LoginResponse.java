package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    private Long refeshExpiresIn;
}
