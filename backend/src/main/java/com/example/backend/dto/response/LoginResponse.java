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

    private UserInformation user;
    private String accessToken;
    private Long expiresIn;
    private String tokenType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInformation{
        private String username;
        private Integer accountID;
        private String role;
    }

}
