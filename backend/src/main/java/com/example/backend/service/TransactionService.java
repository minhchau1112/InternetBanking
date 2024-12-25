package com.example.backend.service;

import com.example.backend.enums.FeePayer;
import com.example.backend.enums.TransactionType;
import com.example.backend.model.Account;
import com.example.backend.model.Transaction;
import com.example.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {

    private static final int FEE = 3000;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void savePendingTransaction(Account source, Account destination, BigDecimal amount, String message, FeePayer feePayer) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setAmount(amount);
        transaction.setMessage(message);
        transaction.setFee(BigDecimal.valueOf(FEE));
        transaction.setFeePayer(feePayer);
        transaction.setOtpVerified(false);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public Optional<Transaction> getPendingTransaction() {
        return transactionRepository.findFirstByStatus("PENDING");
    }

    public void executeTransaction(Transaction transaction) {
        Account source = transaction.getSourceAccount();
        source.setBalance(source.getBalance().subtract(transaction.getAmount()));

        Account destination = transaction.getDestinationAccount();
        destination.setBalance(destination.getBalance().add(transaction.getAmount()));

        if (transaction.getFeePayer().equals(FeePayer.SENDER)) {
            source.setBalance(source.getBalance().subtract(transaction.getFee()));
        } else if (transaction.getFeePayer().equals(FeePayer.RECEIVER)) {
            destination.setBalance(destination.getBalance().subtract(transaction.getFee()));
        }

        transaction.setOtpVerified(true);
        transaction.setStatus("COMPLETED");
        transaction.setCompletedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }
}
