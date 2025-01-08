package com.example.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestResponse<T> {
    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Error code, if any", example = "INVALID_EMAIL")
    private String error;

    @Schema(description = "Response message", example = "Sent OTP code successfully")
    private Object message;

    @Schema(description = "Response data")
    private T data;
}