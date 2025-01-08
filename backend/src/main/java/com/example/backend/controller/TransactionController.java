package com.example.backend.controller;

import com.example.backend.dto.request.OtpVerificationRequest;
import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.InterbankTransactionResponse;
import com.example.backend.dto.response.TransactionResponse;
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
import java.util.ArrayList;
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

    @GetMapping
    public List<?> getTransactions(
            @RequestParam(value = "accountId") String accountId,
            @RequestParam(value = "partnerAccountNumber", required = false) String partnerAccountNumber,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        try {
            Integer srcAccountId = Integer.parseInt(accountId);
            String partnerAccountNum = (partnerAccountNumber != null && !partnerAccountNumber.isEmpty()) ? partnerAccountNumber : null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = (startDate != null && !startDate.isEmpty())
                    ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null;
            LocalDateTime end = (endDate != null && !endDate.isEmpty())
                    ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null;

            List<?> internalTransactions = transactionService.getUserTransactions(srcAccountId, partnerAccountNum, start, end);
            List<?> interbankTransactions = transactionService.getUserInterbankTransactions(srcAccountId, partnerAccountNum, start, end);

            // Combine both lists
            List<Object> allTransactions = new ArrayList<>();
            allTransactions.addAll(internalTransactions);
            allTransactions.addAll(interbankTransactions);

            return allTransactions;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/employee")
    public List<?> getCustomerTransactions(
            @RequestParam(value = "partnerAccountNumber", required = false) String partnerAccountNumber,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        try {
            String partnerAccountNum = (partnerAccountNumber != null && !partnerAccountNumber.isEmpty()) ? partnerAccountNumber : null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = (startDate != null && !startDate.isEmpty())
                    ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null;
            LocalDateTime end = (endDate != null && !endDate.isEmpty())
                    ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null;

            List<?> internalTransactions = transactionService.getUserTransactionsByAccountNumber(partnerAccountNum, start, end);
            List<?> interbankTransactions = transactionService.getUserInterbankTransactionsByAccountNumber(partnerAccountNum, start, end);

            // Combine both lists
            List<Object> allTransactions = new ArrayList<>();
            allTransactions.addAll(internalTransactions);
            allTransactions.addAll(interbankTransactions);

            return allTransactions;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            BigDecimal fee = transactionRequest.getFee();
            BigDecimal amount = transactionRequest.getAmount();

            TransactionRequest newTransactionRequest = TransactionRequest.builder()
                    .sourceAccountId(transactionRequest.getSourceAccountId())
                    .destinationAccountNumber(transactionRequest.getDestinationAccountNumber())
                    .amount(amount)
                    .fee(fee)
                    .feePayer(transactionRequest.getFeePayer())
                    .message(transactionRequest.getMessage())
                    .type(transactionRequest.getType())
                    .build();

            // Lưu giao dịch vào trạng thái chờ xác thực OTP
            Transaction transaction = transactionService.createPendingTransaction(newTransactionRequest);

            // Tạo OTP và gửi qua email
            String otp = transactionService.generateAndSendOTP(transaction);

            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("transactionId", transaction.getId());  // Trả về transactionId
                put("message", "OTP sent to your email.");
            }});
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest otpRequest) {
        boolean isVerified = transactionService.verifyOtpAndCompleteTransaction(otpRequest);

        if (isVerified) {
            return ResponseEntity.ok("Transaction completed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or expired.");
        }
    }
}
