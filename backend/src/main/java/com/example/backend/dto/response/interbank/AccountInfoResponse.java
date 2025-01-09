package com.example.backend.dto.response.interbank;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AccountInfoResponse {
    private AccountInfoData data;
    private String message;
}


