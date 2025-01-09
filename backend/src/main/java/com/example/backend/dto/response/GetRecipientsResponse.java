package com.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetRecipientsResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("alias_name")
    private String aliasName;

    @JsonProperty("bank_code")
    private String bankCode;

    public GetRecipientsResponse(String name, String accountNumber, String aliasName, String bankCode) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.aliasName = aliasName;
        this.bankCode = bankCode;
    }
}
