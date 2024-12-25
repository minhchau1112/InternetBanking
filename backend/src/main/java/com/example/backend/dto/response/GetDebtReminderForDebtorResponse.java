package com.example.backend.dto.response;

import com.example.backend.enums.DebtReminderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GetDebtReminderForDebtorResponse {
    @JsonProperty("debt_reminder_id")
    private Integer debtReminderId;

    @JsonProperty("debt_account_number")
    private String creatorAccountNumber;

    @JsonProperty("debt_name")
    private String creatorName;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private DebtReminderStatus status;

    @JsonProperty("created_time")
    private LocalDateTime createdTime;

    public GetDebtReminderForDebtorResponse(Integer debtReminderId, String creatorAccountNumber, String creatorName, BigDecimal amount, String message, DebtReminderStatus status, LocalDateTime createdTime) {
        this.debtReminderId = debtReminderId;
        this.creatorAccountNumber = creatorAccountNumber;
        this.creatorName = creatorName;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.createdTime = createdTime;
    }
}
