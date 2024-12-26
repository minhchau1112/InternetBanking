package com.example.backend.dto.response;

import com.example.backend.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AccountDetailsResponse {
    private String accountNumber;
    private String accountType;
    private String balance;
    private String createdAt;
    private String ownerName;
    // initialize the fields using the constructor passing Account class
    public AccountDetailsResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.accountType = account.getType().name();
        this.balance = account.getBalance().toString();
        this.createdAt = account.getCreatedAt().toString();
        this.ownerName = account.getCustomer().getName();
    }
}
