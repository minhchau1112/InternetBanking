package com.example.backend.dto.request;
import lombok.Data;

@Data
public class ConfirmTransferRequest {
    private String otp;
    private String email;
    private Integer transactionId;
}
