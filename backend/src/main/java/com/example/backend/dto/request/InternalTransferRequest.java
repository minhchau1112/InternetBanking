package com.example.backend.dto.request;

import com.example.backend.enums.FeePayer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalTransferRequest {
    @JsonProperty("source_account_id")
    private Integer sourceAccountId;

    @JsonProperty("destination_account_id")
    private Integer destinationAccountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("message")
    private String message;

    @JsonProperty("fee_payer")
    private FeePayer feePayer;
}

