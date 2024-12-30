package com.example.backend.service;

import com.example.backend.model.Transaction;
import com.example.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactions(
            Integer sourceAccountId,
            Integer destinationAccountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return transactionRepository.findTransactions(sourceAccountId, destinationAccountId, startDate, endDate);
    }
}
