package com.example.backend.service;

import com.example.backend.dto.request.OtpVerificationRequest;
import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.InterbankTransactionResponse;
import com.example.backend.model.Account;
import com.example.backend.model.InterbankTransaction;
import com.example.backend.model.LinkedBank;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.InterbankTransactionRepository;
import com.example.backend.repository.LinkedBankRepository;
import com.example.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InterbankTransactionRepository interbankTransactionRepository;

    @Autowired
    private LinkedBankRepository linkedBankRepository;

    @Autowired
    private EmailService emailService;

    private final Map<Integer, String> otpCache = new HashMap<>();

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
           String bankName = linkedBankRepository.findByBankCode(transaction.getDestinationBankCode())
                   .map(LinkedBank::getName)
                   .orElse("Unknown Bank");
           return new InterbankTransactionResponse(transaction, bankName);
        }).collect(Collectors.toList());
    }

    public String generateAndSendOTP(Transaction request) {
        System.out.println("generateAndSendOTP"+request);
        String otp = String.valueOf((int) ((Math.random() * 9000) + 1000)); // Generate 4-digit OTP

        // Gửi OTP qua email
        String email = request.getSourceAccount().getCustomer().getEmail();
        emailService.sendEmail(email, "Transaction OTP", "Your OTP is: " + otp);

        // Lưu OTP vào cache hoặc cơ sở dữ liệu (chọn cache cho nhanh)
        otpCache.put(request.getId(), otp);

        return otp;
    }

    public Transaction createPendingTransaction(TransactionRequest request) {
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));


        Transaction transaction = Transaction.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(request.getAmount())
                .fee(request.getFee())
                .feePayer(request.getFeePayer())
                .message(request.getMessage())
                .status("PENDING")
                .otpVerified(false)
                .type(request.getType())
                .createdAt(LocalDateTime.now())
                .build();


        transaction = transactionRepository.save(transaction);

        System.out.println(transaction);
        return transaction;
    }

    public boolean verifyOtpAndCompleteTransaction(OtpVerificationRequest otpRequest) {
        String cachedOtp = otpCache.get(otpRequest.getTransactionId());
        if (cachedOtp != null && cachedOtp.equals(otpRequest.getOtp())) {
            // Hoàn tất giao dịch
            Transaction transaction = transactionRepository.findById(otpRequest.getTransactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
            transaction.setStatus("COMPLETED");
            transaction.setOtpVerified(true);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            // Xóa OTP khỏi cache
            otpCache.remove(otpRequest.getTransactionId());
            return true;
        }
        return false;
    }

}
