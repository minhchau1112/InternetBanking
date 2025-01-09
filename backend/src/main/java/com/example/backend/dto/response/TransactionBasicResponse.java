package com.example.backend.dto.response;

import com.example.backend.model.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionBasicResponse {
    private Integer transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String status;
    private String timestamp;

    public TransactionBasicResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.accountNumber = transaction.getDestinationAccount().getAccountNumber();
        this.amount = transaction.getAmount();
        this.status = transaction.getStatus();
        this.timestamp = transaction.getCompletedAt().toString();
    }
}