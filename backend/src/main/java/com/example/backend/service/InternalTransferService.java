package com.example.backend.service;

import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.InternalTransferRequest;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.DebtReminderNotFoundException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.model.Account;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class InternalTransferService {

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    private final OTPService otpService;
    private final EmailService emailService;

    public InternalTransferService(AccountRepository accountRepository, TransactionService transactionService, OTPService otpService, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Transactional
    public String initiateTransfer(InternalTransferRequest request) throws DebtReminderNotFoundException {
        System.out.println("initiateTransfer");
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new DebtReminderNotFoundException("Source account not found"));

        System.out.println("source_account_id: " + sourceAccount.getId());
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance in source account");
        }

        System.out.println("balance ok");

        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new DebtReminderNotFoundException("Destination account not found"));

        System.out.println("des_account_id: " + destinationAccount.getId());

        String email = sourceAccount.getCustomer().getEmail();
        String otp = String.format("%06d", new Random().nextInt(999999));

        otpService.saveOtp(email, otp, 1);

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("customerName", sourceAccount.getCustomer().getName());
        emailVariables.put("receiverAccountNumber", destinationAccount.getAccountNumber());
        emailVariables.put("receiverFullName", destinationAccount.getCustomer().getName());
        emailVariables.put("transactionAmount", request.getAmount());
        emailVariables.put("otpCode", otp);
        emailService.sendEmailFromTemplateSync(email, "Transaction Transfer OTP Confirmation", "transaction-otp-email-template", emailVariables);


        transactionService.savePendingTransaction(sourceAccount, destinationAccount, request.getAmount(), request.getMessage(), request.getFeePayer());

        return otp;
    }

    @Transactional
    public void confirmTransfer(ConfirmTransferRequest request) throws OTPNotFoundException, EmailNotFoundException {
        String email = request.getEmail();
        String otp = request.getOtp();

        if (email == null || email.isBlank()) {
            throw new EmailNotFoundException("Email can not be blank");
        }

        if (otp == null || otp.isBlank()) {
            throw new OTPNotFoundException("OTP can not be blank");
        }

        String storedOtp = otpService.getOtp(email);

        if (storedOtp == null) {
            throw new OTPNotFoundException("OTP is not valid");
        }

        if (!storedOtp.equals(otp)) {
            throw new OTPNotFoundException("OTP is not valid");
        }

        otpService.deleteOtp(email);

        Transaction pendingTransaction = transactionService.getPendingTransaction()
                .orElseThrow(() -> new IllegalStateException("No pending transaction found"));

        transactionService.executeTransaction(pendingTransaction);
    }
}
