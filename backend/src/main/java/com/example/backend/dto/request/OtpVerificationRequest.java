package com.example.backend.dto.request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private Integer transactionId;
    private String otp;
}
