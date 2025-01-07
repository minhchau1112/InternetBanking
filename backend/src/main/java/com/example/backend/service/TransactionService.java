package com.example.backend.service;

import com.example.backend.dto.response.InterbankTransactionResponse;
import com.example.backend.model.InterbankTransaction;
import com.example.backend.model.LinkedBank;
import com.example.backend.model.Transaction;
import com.example.backend.repository.InterbankTransactionRepository;
import com.example.backend.repository.LinkedBankRepository;
import com.example.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InterbankTransactionRepository interbankTransactionRepository;

    @Autowired
    private LinkedBankRepository linkedBankRepository;

    public List<Transaction> getTransactions(
            Integer sourceAccountId,
            String destinationAccountNum,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
            return transactionRepository.findTransactions(sourceAccountId, destinationAccountNum, startDate, endDate);
    }

    public List<InterbankTransactionResponse> getInterbankTransactionsWithBankName(
            Integer sourceAccountId,
            String destinationAccountNum,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<InterbankTransaction> transactions = interbankTransactionRepository.findInterbankTransactionsWithBankName(sourceAccountId, destinationAccountNum, startDate, endDate);
        return transactions.stream().map(transaction ->{
           String bankName = linkedBankRepository.findByBankCode(transaction.getExternalBankCode())
                   .map(LinkedBank::getName)
                   .orElse("Unknown Bank");
           return new InterbankTransactionResponse(transaction, bankName);
        }).collect(Collectors.toList());
    }



}
