package com.example.backend.controller;

import com.example.backend.dto.request.OtpVerificationRequest;
import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.model.Account;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get the list of transactions for a user.
     * @param accountId ID of the current logged account.
     * @param partnerAccountNumber Account number of the partner.
     * @param type Type of transaction.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @return List of transactions.
     */
    @GetMapping
    public List<?> getTransactions(
            @RequestParam(value = "accountId") String accountId,
            @RequestParam(value = "partnerAccountNumber", required = false) String partnerAccountNumber,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        try {
            Integer srcAccountId = Integer.parseInt(accountId); // Parse accountId to Integer
            String partnerAccountNum = (partnerAccountNumber != null && !partnerAccountNumber.isEmpty()) ? partnerAccountNumber : null; // Set partnerAccountNum to null if it's empty

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // Define the date time format
            LocalDateTime start = (startDate != null && !startDate.isEmpty())
                    ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null; // Parse startDate to LocalDateTime
            LocalDateTime end = (endDate != null && !endDate.isEmpty())
                    ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null; // Parse endDate to LocalDateTime

            // Check if the type is "interbank"
            if ("interbank".equals(type)) {
                return transactionService.getUserInterbankTransactions(srcAccountId, partnerAccountNum, start, end);
            } else {
                return transactionService.getUserTransactions(srcAccountId, partnerAccountNum, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing request: " + e.getMessage());
        }
    }

    /**
     * Create a new transaction.
     * @param transactionRequest Transaction request object.
     * @return Transaction ID and message.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            BigDecimal fee = transactionRequest.getFee(); // Get fee from the request
            BigDecimal amount = transactionRequest.getAmount(); // Get amount from the request

            // Create a new transaction request object
            TransactionRequest newTransactionRequest = TransactionRequest.builder()
                    .sourceAccountId(transactionRequest.getSourceAccountId())
                    .destinationAccountNumber(transactionRequest.getDestinationAccountNumber())
                    .amount(amount)
                    .fee(fee)
                    .feePayer(transactionRequest.getFeePayer())
                    .message(transactionRequest.getMessage())
                    .type(transactionRequest.getType())
                    .build();

            // Save the transaction
            Transaction transaction = transactionService.createPendingTransaction(newTransactionRequest);

            // Generate and send OTP
            String otp = transactionService.generateAndSendOTP(transaction);

            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("transactionId", transaction.getId());  // Trả về transactionId
                put("message", "OTP sent to your email.");
            }});
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Verify OTP and complete the transaction.
     * @param otpRequest OTP verification request.
     * @return Response message.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest otpRequest) {
        boolean isVerified = transactionService.verifyOtpAndCompleteTransaction(otpRequest); // Verify OTP

        if (isVerified) {
            return ResponseEntity.ok("Transaction completed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or expired.");
        }
    }
}
