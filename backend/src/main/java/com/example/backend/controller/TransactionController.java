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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

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

    /**
     * Get the list of transactions for a user.
     * @param accountId ID of the current logged account.
     * @param partnerAccountNumber Account number of the partner.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @return List of transactions.
     */
    @Operation(summary = "Get transactions", description = "Retrieve transactions based on filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public List<?> getTransactions(
            @RequestParam(value = "accountId") String accountId,
            @RequestParam(value = "partnerAccountNumber", required = false) String partnerAccountNumber,
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

    /**
     * Get the list of transactions for a customer.
     * @param partnerAccountNumber Account number of the partner.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @return List of transactions.
     */
    @Operation(summary = "Get transactions", description = "Retrieve transactions based on filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
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

    /**
     * Get the list of interbank transactions.
     * @param startDate Start date of the transaction.
     * @param endDate End date of the transaction.
     * @param bankCode Bank code of the partner bank.
     * @return List of interbank transactions.
     */
    @Operation(summary = "Get interbank transactions", description = "Retrieve interbank transactions based on filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of interbank transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/interbank-transactions/filter")
    public List<InterbankTransactionResponse> getInterbankTransactions(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "bankCode", required = false) String bankCode
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime start = (startDate != null && !startDate.isEmpty())
                ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty())
                ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null;
        String bankCodeStr = (bankCode != null && !bankCode.isEmpty()) ? bankCode : null;

        return transactionService.filterInterbankTransactions(start, end, bankCodeStr);
    }

    /**
     * Create a new transaction.
     * @param transactionRequest Transaction request object.
     * @return Transaction ID and message.
     */
    @Operation(summary = "Create a new transaction", description = "Initiate a new transaction and send OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created and OTP sent",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transaction details", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
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
    @Operation(summary = "Verify OTP", description = "Verify the OTP and complete the transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
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
