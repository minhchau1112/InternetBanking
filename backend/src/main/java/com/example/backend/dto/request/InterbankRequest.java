package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InterbankRequest {
    private String bankCode;
    private String payload; // Encrypted data
    private String signature; // Signature of the payload
    @JsonProperty("AESKey")
    private String AESKey; // Encrypted AES key

    public InterbankRequest() {
    }

    public InterbankRequest(String payload, String signature,String bankCode, String AES) {
        this.payload = payload;
        this.signature = signature;
        this.bankCode = bankCode;
        this.AESKey = AES;
    }

    @Override
    public String toString() {
        return "InterbankRequest{" +
                "payload='" + payload + '\'' +
                ", signature='" + signature + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}

