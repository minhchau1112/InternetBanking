package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipientUpdateRequest {

    private Integer recipientId;
    private String accountNumber;
    private String aliasName;
    private String bankCode;

}
