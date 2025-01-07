package com.example.backend.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterbankResponse {
    private String payload; // Encrypted response data
    private String signature; // Signature of the response
    private String responseCode; // Status of the response (e.g., SUCCESS, ERROR)
    private String keyType; // Type of encryption used (e.g., RSA, PGP)

    public InterbankResponse() {
    }

    public InterbankResponse(String payload, String signature, String responseCode, String keyType) {
        this.payload = payload;
        this.signature = signature;
        this.responseCode = responseCode;
        this.keyType = keyType;
    }

    @Override
    public String toString() {
        return "InterbankResponse{" +
                "payload='" + payload + '\'' +
                ", signature='" + signature + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", keyType='" + keyType + '\'' +
                '}';
    }
}

