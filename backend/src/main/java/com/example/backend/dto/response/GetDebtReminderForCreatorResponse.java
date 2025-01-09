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
    @JsonProperty("debt_reminder_id")
    private Integer debtReminderId;

    @JsonProperty("debt_account_number")
    private String debtAccountNumber;

    @JsonProperty("debt_name")
    private String debtName;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private DebtReminderStatus status;

    @JsonProperty("created_time")
    private LocalDateTime createdTime;

    public GetDebtReminderForCreatorResponse(Integer debtReminderId, String debtAccountNumber, String debtName, BigDecimal amount, String message, DebtReminderStatus status, LocalDateTime createdTime) {
        this.debtReminderId = debtReminderId;
        this.debtAccountNumber = debtAccountNumber;
        this.debtName = debtName;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.createdTime = createdTime;
    }
}
