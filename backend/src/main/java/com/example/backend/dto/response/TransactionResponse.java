package com.example.backend.dto.response;

import com.example.backend.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TransactionResponse {
    private Transaction transaction;
    private String tab;
}
