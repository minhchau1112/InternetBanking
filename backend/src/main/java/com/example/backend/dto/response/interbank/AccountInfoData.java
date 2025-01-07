package com.example.backend.dto.response.interbank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountInfoData {
    private String firstName;
    private String lastName;
    private String bankCode;
    private String accountNumber;
    // Getters and setters
}
