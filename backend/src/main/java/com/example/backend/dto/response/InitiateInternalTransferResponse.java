package com.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InitiateInternalTransferResponse {
    @JsonProperty("otp")
    private String otp;

    @JsonProperty("transaction_id")
    private Integer transactionId;

    public InitiateInternalTransferResponse(String otp, Integer transactionId) {
        this.otp = otp;
        this.transactionId = transactionId;
    }
}
