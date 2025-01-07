package com.example.backend.dto.response.interbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoRequest {
    // Mapping the request from the interbank
    @JsonProperty("bank_code")
    private String bankCode;
    @JsonProperty("account_number")
    private String accountNumber;

    // to String
    @Override
    public String toString() {
        return "AccountInfoRequest{" +
                "bankCode=" + '"' + bankCode + '"' +
                ",accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
