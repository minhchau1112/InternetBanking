package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipientUpdateRequest {

    @NotNull(message = "Recipient ID is required")
    private Integer recipientId;

    @NotBlank(message = "Account number is required")
    private String accountNumber;
    private String aliasName;
    private String bankCode;

}
