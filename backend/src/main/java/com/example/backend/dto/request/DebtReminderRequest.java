package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DebtReminderRequest {

    @JsonProperty("creator_account_id")
    private Integer creatorAccountId;

    @JsonProperty("debtor_account_id")
    private Integer debtorAccountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("message")
    private String message;
}
