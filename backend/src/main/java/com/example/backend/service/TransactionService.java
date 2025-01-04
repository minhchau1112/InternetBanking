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

    private static final int FEE = 3000;
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
