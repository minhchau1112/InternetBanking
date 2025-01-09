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
import com.example.backend.dto.response.TransactionResponse;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

    /**
     * Get the list of transactions for a user.
     * @param accountId ID of the current logged account.
     * @param partnerAccountNumber Account number of the partner.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @return List of transactions.
     */
    public List<TransactionResponse> getUserTransactions(
            Integer accountId,
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccount(accountId, partnerAccountNumber, startDate, endDate);

        return transactions.stream().map(transaction -> {
            String tab = transaction.getSourceAccount().getId().equals(accountId) ? "out" : "in";
            System.out.println(transaction.getType());
            if(transaction.getType().toString().equals("DEPOSIT")) {
                tab = "in";
            }
            return new TransactionResponse(transaction, tab);
        }).collect(Collectors.toList());
    }

    /**
     * Get the list of interbank transactions for a user.
     * @param accountId ID of the current logged account.
     * @param partnerAccountNumber Account number of the partner.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @return List of interbank transactions.
     */
    public List<InterbankTransactionResponse> getUserInterbankTransactions(
            Integer accountId,
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<InterbankTransaction> transactions = interbankTransactionRepository.findInterbankTransactionsByAccount(accountId, partnerAccountNumber, startDate, endDate);

        return transactions.stream().map(transaction -> {
            String tab = transaction.isIncoming() ? "in" : "out";
            String externalAccount = transaction.getExternalAccountNumber();
            String bankName = linkedBankRepository.findByBankCode(transaction.getExternalBankCode())
                    .map(LinkedBank::getName)
                    .orElse("Unknown Bank");
            return new InterbankTransactionResponse(transaction, externalAccount, bankName, tab);
        }).collect(Collectors.toList());
    }

    public List<InterbankTransactionResponse> getInterbankTransactionsWithAccountId(
            Integer accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<InterbankTransaction> transactions = interbankTransactionRepository.findInterbankTransactionsWithAccountId(accountId, startDate, endDate);
        return transactions.stream().map(transaction -> {
            String tab = transaction.isIncoming() ? "in" : "out";
            String externalAccount = transaction.isIncoming() ? transaction.getExternalAccountNumber() : transaction.getSourceAccount().getAccountNumber();
            String bankName = linkedBankRepository.findByBankCode(transaction.getExternalBankCode())
                    .map(LinkedBank::getName)
                    .orElse("Unknown Bank");
            return new InterbankTransactionResponse(transaction, externalAccount, bankName, tab);
        }).collect(Collectors.toList());
    }

    /**
     * Generate and send OTP for a transaction.
     * @param request Transaction request.
     * @return OTP.
     */
    public String generateAndSendOTP(Transaction request) {
        String otp = String.valueOf((int) ((Math.random() * 9000) + 1000)); // Generate 4-digit OTP

        // Gửi OTP qua email
        String email = request.getSourceAccount().getCustomer().getEmail();
//        emailService.sendEmail(email, "Transaction OTP", "Your OTP is: " + otp);
        Map<String, Object> variables = new HashMap<>();
        variables.put("OTP_CODE", otp);
        emailService.sendEmailFromTemplateSync(email, "Transaction OTP", "otp", variables);

        // Lưu OTP vào cache hoặc cơ sở dữ liệu (chọn cache cho nhanh)
        otpCache.put(request.getId(), otp);

        return otp;
    }

    /**
     * Create a new transaction in the PENDING state.
     * @param request Transaction request.
     * @return Created transaction.
     */
    public Transaction createPendingTransaction(TransactionRequest request) {
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        if(String.valueOf(request.getType()).equals("TRANSFER") || String.valueOf(request.getType()).equals("DEPOSIT")) {
            boolean hasBalance = hasSufficientBalance(sourceAccount, request.getAmount(), request.getFee(), String.valueOf(request.getFeePayer()));
            if(!hasBalance) {
                throw new IllegalArgumentException("Insufficient balance for the transaction");
            }
        }

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

    /**
     * Verify OTP and complete the transaction.
     * @param otpRequest OTP verification request.
     * @return True if the OTP is verified and the transaction is completed.
     */
    public boolean verifyOtpAndCompleteTransaction(OtpVerificationRequest otpRequest) {
        String cachedOtp = otpCache.get(otpRequest.getTransactionId());
        if (cachedOtp != null && cachedOtp.equals(otpRequest.getOtp())) {
            // Hoàn tất giao dịch
            Transaction transaction = transactionRepository.findById(otpRequest.getTransactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            // Kiểm tra loại giao dịch
            if (String.valueOf(transaction.getType()).equals("TRANSFER") || String.valueOf(transaction.getType()).equals("DEPOSIT")) {
                BigDecimal amount = transaction.getAmount();
                BigDecimal fee = transaction.getFee();
                String feePayer = String.valueOf(transaction.getFeePayer());

                Account sourceAccount = transaction.getSourceAccount();
                Account destinationAccount = transaction.getDestinationAccount();

                // Cập nhật số dư cho tài khoản nguồn
                BigDecimal totalDeduction = feePayer.equals("SENDER") ? amount.add(fee) : amount;
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(totalDeduction));

                // Cập nhật số dư cho tài khoản đích
                totalDeduction = feePayer.equals("RECEIVER") ? amount.subtract(fee) : amount;
                destinationAccount.setBalance(destinationAccount.getBalance().add(totalDeduction));

                accountRepository.save(sourceAccount);
                accountRepository.save(destinationAccount);
            }

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
        transaction.setFee(BigDecimal.valueOf(3000));
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
    /**
     * Check if the account has sufficient balance for the transaction.
     * @param account Account.
     * @param amount Amount to be deducted.
     * @param fee Transaction fee.
     * @param feePayer Fee payer.
     * @return True if the account has sufficient balance.
     */
    public boolean hasSufficientBalance(Account account, BigDecimal amount, BigDecimal fee, String feePayer) {
        BigDecimal totalDeduction = feePayer.equals("SENDER") ? amount.add(fee) : amount;
        return account.getBalance().compareTo(totalDeduction) >= 0;
    }


    // Lấy danh sách giao dịch nội bộ của tài khoản dua vao account number cho employee
    public List<TransactionResponse> getUserTransactionsByAccountNumber(
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountNumber(partnerAccountNumber, startDate, endDate);

        return transactions.stream().map(transaction -> {
            String tab = transaction.getSourceAccount().getAccountNumber().equals(partnerAccountNumber) ? "out" : "in";
            System.out.println(transaction.getType());
            if(transaction.getType().toString().equals("DEPOSIT")) {
                tab = "in";
            }
            return new TransactionResponse(transaction, tab);
        }).collect(Collectors.toList());
    }

    // Lấy danh sách giao dịch liên ngân hàng của tài khoản
    public List<InterbankTransactionResponse> getUserInterbankTransactionsByAccountNumber(
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Account account = accountRepository.findByAccountNumber(partnerAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        List<InterbankTransaction> transactions = interbankTransactionRepository.findInterbankTransactionsByAccountNumber(account.getId(), startDate, endDate);

        return transactions.stream().map(transaction -> {
            String tab = transaction.isIncoming() ? "in" : "out";
            String externalAccount = transaction.getExternalAccountNumber();
            String bankName = linkedBankRepository.findByBankCode(transaction.getExternalBankCode())
                    .map(LinkedBank::getName)
                    .orElse("Unknown Bank");
            return new InterbankTransactionResponse(transaction, externalAccount, bankName, tab);
        }).collect(Collectors.toList());
    }



}
