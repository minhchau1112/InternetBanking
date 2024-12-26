package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DepositRequest {
    @JsonProperty("username")
    private String username; // Optional, either this or accountNumber is required
    @JsonProperty("account_number")
    private String accountNumber; // Optional, either this or username is required
    @JsonProperty("deposit_amount")
    private double depositAmount; // Required

    // Validation to ensure one of username or accountNumber is provided
    public boolean isValid() {
        return ((username != null && !username.isEmpty() ) ||
                (accountNumber != null && !accountNumber.isEmpty()) ) &&
                depositAmount > 0;
    }
}

