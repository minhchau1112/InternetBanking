package com.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InitiateInternalTransferResponse {
    @JsonProperty("otp")
    private String otp;

    public InitiateInternalTransferResponse(String otp) {
        this.otp = otp;
    }
}
