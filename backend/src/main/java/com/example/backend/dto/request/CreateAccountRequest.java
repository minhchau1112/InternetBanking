package com.example.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @Schema(description = "Role of the user", example = "customer")
    private String role;

    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phone;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;
}
