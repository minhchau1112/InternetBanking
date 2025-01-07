package com.example.backend.dto.response.interbank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailResponse {
    private String name;
    private String bankCode;
    private String accountNumber;
    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
