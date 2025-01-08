package com.example.backend.dto.response;

import com.example.backend.model.InterbankTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InterbankTransactionResponse {
    private InterbankTransaction transaction;
    private String externalAccount;
    private String bankName;
    private String tab;
}