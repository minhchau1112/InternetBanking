package com.example.backend.dto.request;

import com.example.backend.enums.FeePayer;
import com.example.backend.enums.TransactionType;
import com.example.backend.model.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TransactionRequest {
    private Integer sourceAccountId;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private FeePayer feePayer;
    private String message;
    private TransactionType type;
}