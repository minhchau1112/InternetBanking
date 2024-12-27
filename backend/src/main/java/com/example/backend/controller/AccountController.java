package com.example.backend.controller;

import com.example.backend.dto.request.DepositRequest;
import com.example.backend.dto.response.AccountDetailsResponse;
import com.example.backend.model.ApiResponse;
import com.example.backend.model.Customer;
import com.example.backend.model.Transaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Account;
import com.example.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Get the list of accounts for a customer.
     * @param customerId ID of the customer.
     * @return List of accounts belonging to the customer.
     */
    @GetMapping("/list/{customer_id}")
    public ResponseEntity<ApiResponse<List<AccountDetailsResponse>>> getAccountsByCustomerId(@PathVariable(
            "customer_id") String customerId) {
        Optional<Account> accounts = accountService.findByCustomerId(Integer.valueOf(customerId));
        // map list of accounts to AccountDetailsReponse
        List<AccountDetailsResponse> accountDetailsResponses = accounts.stream()
                .map(AccountDetailsResponse::new) // Create a new AccountDetailsResponse for each Account
                .toList();
        ApiResponse<List<AccountDetailsResponse>> response = new ApiResponse<>(
                200,                           // Status code
                null,                          // No error
                "Accounts fetched successfully", // Success message
                accountDetailsResponses        // Data
        );

        return ResponseEntity.ok(response);

    }

    /**
     * Create a new account for a customer.
     * @param requestBody Request body containing customer details.
     * @return Created account.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createAccount(@RequestBody Map<String, String> requestBody) {
        String role = requestBody.get("role");
        String name = requestBody.get("name");
        String email = requestBody.get("email");
        String phone = requestBody.get("phone");
        String username = requestBody.get("username");
        System.out.println("role: " + role);
        if (!"customer".equals(role)) {
            return ResponseEntity.badRequest().body(null); // Only customers are allowed.
        }

        Customer createdAccount = accountService.createAccount(username, name, email, phone);
        ApiResponse<Customer> response = new ApiResponse<>(
                201,                           // Status code
                null,                          // No error
                "Account created successfully", // Success message
                createdAccount        // Data
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Look up account information from another bank.
     * @param lookupRequest Request body containing lookup details.
     * @return Account information or error response.
     */
//    @PostMapping("/lookup")
//    public ResponseEntity<Map<String, Object>> lookupAccount(@RequestBody Map<String, String> lookupRequest) {
//        String usernameOrAccountNumber = lookupRequest.get("username") != null ?
//                lookupRequest.get("username") : lookupRequest.get("account_number");
//        String signature = lookupRequest.get("signature");
//
//        if (usernameOrAccountNumber == null || signature == null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
//        }
//
//        Map<String, Object> accountInfo = accountService.lookupAccount(usernameOrAccountNumber, signature);
//        return ResponseEntity.ok(accountInfo);
//    }

    /**
     * Get detailed account information by account ID.
     * @param accountNumber ID of the account.
     * @return Account details including balance.
     */
    @GetMapping("/{account_number}")
    public AccountDetailsResponse getAccountDetails(@PathVariable(
            "account_number") String accountNumber) {
        Account accountDetails = accountService.getAccountDetails(accountNumber);
        return new AccountDetailsResponse(accountDetails);  // Convert Account to AccountDetailsResponse
    }

    /**
     * Get detailed account information by account ID.
     * @param accountNumber ID of the account.
     * @return Account details including balance.
     */
    @GetMapping("/username/{username}")
    public AccountDetailsResponse getAccountDetailsByUsername(@PathVariable(
            "username") String username) {
        Account accountDetails = accountService.getAccountDetailsByUsername(username);
        return new AccountDetailsResponse(accountDetails);  // Convert Account to AccountDetailsResponse
    }

    /**
     * Deposit money into account.
     * @param depositRequest Request body containing deposit details include username,
     *                        depositAmount and accountNumber.
     * @return Success or error response.
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Transaction>> deposit(@RequestBody DepositRequest depositRequest) {
        String username = depositRequest.getUsername();
        String depositAmount = String.valueOf(depositRequest.getDepositAmount());
        String accountNumber = depositRequest.getAccountNumber();
        System.out.println("username: " + username);
        System.out.println("depositAmount: " + depositAmount);
        System.out.println("accountNumber: " + accountNumber);
        if (!depositRequest.isValid()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    400,                           // Status code
                    "Invalid request",             // Error message
                    "Invalid deposit request",     // Success message
                    null                           // No data
            ));
        }

        Transaction transaction = accountService.deposit(depositRequest);
        // temp return
        return ResponseEntity.ok(new ApiResponse<>(
                201,                           // Status code
                null,                          // No error
                "Deposit successful",          // Success message
                transaction                    // Data
        ));
    }
}


