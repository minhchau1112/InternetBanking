package com.example.backend.dto.response;

import com.example.backend.enums.DebtReminderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GetDebtReminderForCreatorResponse {
    @JsonProperty("debt_account_number")
    private String debtAccountNumber;

    @JsonProperty("debt_name")
    private String debtName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private DebtReminderStatus status;

    @JsonProperty("created_time")
    private LocalDateTime createdTime;

    public GetDebtReminderForCreatorResponse(String debtAccountNumber, String debtName, String email, BigDecimal amount, String message, DebtReminderStatus status, LocalDateTime createdTime) {
        this.debtAccountNumber = debtAccountNumber;
        this.debtName = debtName;
        this.email = email;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.createdTime = createdTime;
    }
}
