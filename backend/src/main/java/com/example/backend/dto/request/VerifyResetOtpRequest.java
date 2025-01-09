package com.example.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResetOtpRequest {
    @Schema(description = "User's email", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "OTP code sent to the user", example = "193009", required = true)
    private String otp;

}
