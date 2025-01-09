package com.example.backend.controller;

import com.example.backend.dto.request.DepositRequest;
import com.example.backend.dto.response.AccountDetailsResponse;
import com.example.backend.model.Customer;
import com.example.backend.model.Transaction;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get list of accounts for a customer", description = "Fetch all accounts belonging to a specific customer by their customer ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "No accounts found for the customer")
    })
    @GetMapping("/list/{customer_id}")
    public ResponseEntity<List<AccountDetailsResponse>> getAccountsByCustomerId(
            @PathVariable("customer_id") String customerId) {
        Optional<Account> accounts = accountService.findByCustomerId(Integer.valueOf(customerId));
        List<AccountDetailsResponse> accountDetailsResponses = accounts.stream()
                .map(AccountDetailsResponse::new)
                .toList();
        System.out.println("accountDetailsResponses: " + accountDetailsResponses);
        return ResponseEntity.ok(accountDetailsResponses);
    }

    @Operation(summary = "Create a new account", description = "Create a new account for a customer with the given details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Customer> createAccount(@RequestBody Map<String, String> requestBody) {
        String role = requestBody.get("role");
        String name = requestBody.get("name");
        String email = requestBody.get("email");
        String phone = requestBody.get("phone");
        String username = requestBody.get("username");

        if (!"customer".equals(role)) {
            throw new IllegalArgumentException("Invalid role");
        }

        return ResponseEntity.ok(accountService.createAccount(username, name, email, phone));
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
    @Operation(summary = "Get account details by account number", description = "Fetch detailed information about an account using its account number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{account_number}")
    public ResponseEntity<AccountDetailsResponse> getAccountDetails(@PathVariable(
            "account_number") String accountNumber) {
        Account accountDetails = accountService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(new AccountDetailsResponse(accountDetails));  // Convert Account
        // to AccountDetailsResponse
    }

    @DeleteMapping("/{account_number}")
    public ResponseEntity<AccountDetailsResponse> deleteAccount(@PathVariable(
            "account_number") String accountNumber) {
        Account accountDetails = accountService.deleteAccount(accountNumber);
        return ResponseEntity.ok(new AccountDetailsResponse(accountDetails));  // Convert Account
        // to AccountDetailsResponse
    }

    /**
     * Get detailed account information by username.
     * @param username username of the customer.
     * @return Account details including balance.
     */
    @Operation(summary = "Get account details by username", description = "Fetch detailed information about an account using the customer's username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<AccountDetailsResponse> getAccountDetailsByUsername(@PathVariable(
            "username") String username) {
        Account accountDetails = accountService.getAccountDetailsByUsername(username);
        return ResponseEntity.ok(new AccountDetailsResponse(accountDetails));  // Convert Account
        // into AccountDetailsResponse
    }

    /**
     * Get detailed account information by username.
     * @param accountId userId of the customer.
     * @return Account details including balance.
     */
    @Operation(summary = "Get account details by username", description = "Fetch detailed information about an account using the customer's username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/accountInfo/{accountId}")
    public ResponseEntity<AccountDetailsResponse> getAccountDetailsByUserId(@PathVariable(
            "accountId") String accountId) {
        System.out.println("accountId: " + accountId);
        Account accountDetails = accountService.getAccountDetailsByAccountId(accountId);
        System.out.println("accountDetails: " + accountDetails);
        return ResponseEntity.ok(new AccountDetailsResponse(accountDetails));  // Convert Account
        // into AccountDetailsResponse
    }

    /**
     * Deposit money into account.
     * @param depositRequest Request body containing deposit details include username,
     *                        depositAmount and accountNumber.
     * @return Success or error response.
     */
    @Operation(summary = "Deposit money into an account", description = "Deposit a specified amount into an account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "400", description = "Invalid deposit request")
    })
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the deposit request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DepositRequest.class),
                            examples = @ExampleObject(
                                    name = "DepositRequest",
                                    value = "{ \"username\": \"john_doe\", \"deposit_amount\": 1000000, \"account_number\": \"123456789\" }"
                            )
                    )
            )
            @RequestBody DepositRequest depositRequest) {
        String username = depositRequest.getUsername();
        String depositAmount = String.valueOf(depositRequest.getDepositAmount());
        String accountNumber = depositRequest.getAccountNumber();
        System.out.println("username: " + username);
        System.out.println("depositAmount: " + depositAmount);
        System.out.println("accountNumber: " + accountNumber);
        if (!depositRequest.isValid()) {
            return ResponseEntity.badRequest().body(null);
        }

        Transaction transaction = accountService.deposit(depositRequest);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/v2/{accountNumber}")
    @APIMessage("Retrieve account by account number success")
    public ResponseEntity<Account> findAccountByAccountNumber(@PathVariable String accountNumber) throws com.example.backend.exception.AccountNotFoundException {
        Account account = accountService.getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(account);
    }
}


