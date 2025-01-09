package com.example.backend.controller;

import com.example.backend.dto.request.CreateAccountRequest;
import com.example.backend.dto.request.DepositRequest;
import com.example.backend.dto.response.AccountDetailsResponse;
import com.example.backend.dto.response.RestResponse;
import com.example.backend.dto.response.TransactionBasicResponse;
import com.example.backend.model.Customer;
import com.example.backend.model.Transaction;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Get list of accounts for a customer",
            description = "Fetch all accounts belonging to a specific customer by their customer ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of accounts retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"List of accounts retrieved successfully.\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"accountNumber\": \"123456789\",\n" +
                                    "      \"accountType\": \"SAVINGS\",\n" +
                                    "      \"balance\": \"1000.0\",\n" +
                                    "      \"createdAt\": \"2023-10-01T12:00:00Z\",\n" +
                                    "      \"ownerName\": \"John Doe\",\n" +
                                    "      \"isPrimary\": true\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts found for the customer",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"NO_ACCOUNTS_FOUND\",\n" +
                                    "  \"message\": \"No accounts found for the customer.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
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

    @Operation(
            summary = "Create a new account",
            description = "Create a new account for a customer with the given details."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"CALL API SUCCESS\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"id\": 17,\n" +
                                    "    \"username\": \"john_doe1\",\n" +
                                    "    \"password\": \"r#s$CqvGcxje\",\n" +
                                    "    \"createdAt\": \"2025-01-09T15:20:37.2322932\",\n" +
                                    "    \"name\": \"John Doe\",\n" +
                                    "    \"email\": \"john.doe1@example.com\",\n" +
                                    "    \"phone\": \"12345678901\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"error\": \"INVALID_INPUT\",\n" +
                                    "  \"message\": \"Invalid input.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Customer> createAccount(@RequestBody CreateAccountRequest request) {
        if (!"customer".equals(request.getRole())) {
            throw new IllegalArgumentException("Invalid role");
        }

        return ResponseEntity.ok(accountService.createAccount(
                request.getUsername(),
                request.getName(),
                request.getEmail(),
                request.getPhone()
        ));
    }


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

    @Operation(
            summary = "Get account details by account number",
            description = "Fetch detailed information about an account using its account number."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account details retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Account details retrieved successfully.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"accountNumber\": \"123456789\",\n" +
                                    "    \"balance\": 1000.0,\n" +
                                    "    \"currency\": \"USD\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"ACCOUNT_NOT_FOUND\",\n" +
                                    "  \"message\": \"Account not found.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
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
    @Operation(
            summary = "Get account details by username",
            description = "Fetch detailed information about an account using the customer's username."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account details retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Account details retrieved successfully.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"accountNumber\": \"123456789\",\n" +
                                    "    \"balance\": 1000.0,\n" +
                                    "    \"currency\": \"USD\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"ACCOUNT_NOT_FOUND\",\n" +
                                    "  \"message\": \"Account not found.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
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
    @Operation(summary = "Get account details by accountId", description = "Fetch detailed information about an account using the customer's accountId.")
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
    @Operation(
            summary = "Deposit money into an account",
            description = "Deposit a specified amount into an account."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Deposit successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Deposit successful.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"transactionId\": \"123456\",\n" +
                                    "    \"accountNumber\": \"123456789\",\n" +
                                    "    \"amount\": 1000000,\n" +
                                    "    \"status\": \"COMPLETED\",\n" +
                                    "    \"timestamp\": \"2023-10-01T12:00:00Z\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid deposit request",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"error\": \"INVALID_REQUEST\",\n" +
                                    "  \"message\": \"Invalid deposit request.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    @PostMapping("/deposit")
    public ResponseEntity<TransactionBasicResponse> deposit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the deposit request, only 1 username or account_number will have value, the other will be null",
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
        // return basic information of the transaction
        TransactionBasicResponse transactionBasicResponse = new TransactionBasicResponse(transaction);

        return ResponseEntity.ok(transactionBasicResponse);
    }

    @Operation(
            summary = "Retrieve account by account number",
            description = "Fetches an account based on the provided account number.",
            parameters = {
                    @Parameter(
                            name = "accountNumber",
                            description = "The account number to retrieve the account information",
                            required = true,
                            example = "345562455"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Retrieve account by account number success",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Account found",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Retrieve account by account number success\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"id\": 3,\n" +
                                                    "    \"customer\": {\n" +
                                                    "      \"id\": 3,\n" +
                                                    "      \"username\": \"nlqkhanh\",\n" +
                                                    "      \"password\": \"$2a$10$0SsupHTuP8RS17QUbynsRuVcHnjmnQhqeiY08Mm2.fhbsMP99W/QS\",\n" +
                                                    "      \"createdAt\": \"2024-12-22T00:05:53\",\n" +
                                                    "      \"name\": \"Quốc Khánh\",\n" +
                                                    "      \"email\": \"chauhhcc@gmail.com\",\n" +
                                                    "      \"phone\": \"034724892\"\n" +
                                                    "    },\n" +
                                                    "    \"accountNumber\": \"345562455\",\n" +
                                                    "    \"balance\": 12500000.00,\n" +
                                                    "    \"isPrimary\": true,\n" +
                                                    "    \"type\": \"CHECKING\",\n" +
                                                    "    \"createdAt\": \"2024-12-22T00:19:39\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Account not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Account not found\",\n" +
                                                    "  \"message\": \"The account was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/v2/{accountNumber}")
    @APIMessage("Retrieve account by account number success")
    public ResponseEntity<Account> findAccountByAccountNumber(@PathVariable String accountNumber) throws com.example.backend.exception.AccountNotFoundException {
        Account account = accountService.getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(account);
    }
}


