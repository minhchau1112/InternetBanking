package com.example.backend.service;

import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.InternalTransferRequest;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.Account;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalTransferService {

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    private final OTPService otpService;

    public InternalTransferService(AccountRepository accountRepository, TransactionService transactionService, OTPService otpService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.otpService = otpService;
    }

    @Transactional
    public String initiateTransfer(InternalTransferRequest request) {
        System.out.println("initiateTransfer");
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new NotFoundException("Source account not found"));

        System.out.println("source_account_id: " + sourceAccount.getId());
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance in source account");
        }

        System.out.println("balance ok");

        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new NotFoundException("Destination account not found"));

        System.out.println("des_account_id: " + destinationAccount.getId());

        String otp = otpService.generateAndSendOTP(sourceAccount.getCustomer().getEmail());

        transactionService.savePendingTransaction(sourceAccount, destinationAccount, request.getAmount(), request.getMessage(), request.getFeePayer());

        return otp;
    }

    @Transactional
    public void confirmTransfer(ConfirmTransferRequest request) {
        if (!otpService.verifyOTP(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        Transaction pendingTransaction = transactionService.getPendingTransaction()
                .orElseThrow(() -> new IllegalStateException("No pending transaction found"));

        transactionService.executeTransaction(pendingTransaction);
    }
}
