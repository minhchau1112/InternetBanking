package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipientCreateRequest {

    private Integer customerId;
    private String accountNumber;
    private String aliasName;
    private String bankCode;

}
