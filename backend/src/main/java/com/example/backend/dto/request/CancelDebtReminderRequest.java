package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CancelDebtReminderRequest {
    @JsonProperty("reason")
    private String cancellationReason;
}
