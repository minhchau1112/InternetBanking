package com.example.backend.controller;

import com.example.backend.dto.request.CancelDebtReminderRequest;
import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.DebtReminderRequest;
import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.exception.DebtReminderNotFoundException;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.InsufficientBalanceException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.model.DebtReminder;
import com.example.backend.service.DebtReminderService;
import com.example.backend.service.InternalTransferService;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/debt-reminders")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;
    private final InternalTransferService internalTransferService;

    public DebtReminderController(DebtReminderService debtReminderService, InternalTransferService internalTransferService) {
        this.debtReminderService = debtReminderService;
        this.internalTransferService = internalTransferService;
    }

    @Operation(
            summary = "Create a new debt reminder",
            description = "This API creates a new debt reminder by providing details about the creator, debtor, amount, and message.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Debt reminder details to be created",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body with valid data",
                                    value = "{\n" +
                                            "  \"creator_account_id\": 3,\n" +
                                            "  \"debtor_account_id\": 4,\n" +
                                            "  \"amount\": 600000,\n" +
                                            "  \"message\": \"Please pay me back!\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Debt reminder created successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Debt reminder created successfully",
                                            value = "{\n" +
                                                    "  \"status\": 201,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Create debt reminder success\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"id\": 26,\n" +
                                                    "    \"creatorAccount\": {\n" +
                                                    "      \"id\": 3,\n" +
                                                    "      \"customer\": {\n" +
                                                    "        \"id\": 3,\n" +
                                                    "        \"username\": \"nlqkhanh\",\n" +
                                                    "        \"name\": \"Quốc Khánh\",\n" +
                                                    "        \"email\": \"chauhhcc@gmail.com\",\n" +
                                                    "        \"phone\": \"034724892\"\n" +
                                                    "      },\n" +
                                                    "      \"accountNumber\": \"345562455\",\n" +
                                                    "      \"balance\": 194270000.00,\n" +
                                                    "      \"isPrimary\": true,\n" +
                                                    "      \"type\": \"CHECKING\",\n" +
                                                    "      \"createdAt\": \"2024-12-22T00:19:39\"\n" +
                                                    "    },\n" +
                                                    "    \"debtorAccount\": {\n" +
                                                    "      \"id\": 4,\n" +
                                                    "      \"customer\": {\n" +
                                                    "        \"id\": 4,\n" +
                                                    "        \"username\": \"ntson\",\n" +
                                                    "        \"name\": \"Thanh Sơn\",\n" +
                                                    "        \"email\": \"tnmchau1211@gmail.com\",\n" +
                                                    "        \"phone\": \"034724892\"\n" +
                                                    "      },\n" +
                                                    "      \"accountNumber\": \"984837497\",\n" +
                                                    "      \"balance\": 7691000.00,\n" +
                                                    "      \"isPrimary\": true,\n" +
                                                    "      \"type\": \"CHECKING\",\n" +
                                                    "      \"createdAt\": \"2024-12-22T00:20:31\"\n" +
                                                    "    },\n" +
                                                    "    \"amount\": 600000,\n" +
                                                    "    \"message\": \"Please pay me back!\",\n" +
                                                    "    \"status\": \"PENDING\",\n" +
                                                    "    \"createdAt\": \"2025-01-09T12:59:19.4133854\",\n" +
                                                    "    \"updatedAt\": \"2025-01-09T12:59:19.4133854\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Debtor account not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Debtor account not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Debtor account not found\",\n" +
                                                    "  \"message\": \"The debt reminder was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @APIMessage("Create debt reminder success")
    public ResponseEntity<DebtReminder> createDebtReminder(@RequestBody DebtReminderRequest request) throws DebtReminderNotFoundException {
        DebtReminder reminder = debtReminderService.createDebtReminder(
                request.getCreatorAccountId(),
                request.getDebtorAccountId(),
                request.getAmount(),
                request.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reminder);
    }


    @Operation(
            summary = "Get debt reminders for a creator",
            description = "Retrieve a paginated list of debt reminders created by a specific creator account. Optionally filter by debt reminder status.",
            parameters = {
                    @Parameter(
                            name = "creatorAccountId",
                            description = "ID of the creator account",
                            required = true,
                            example = "4"
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filter reminders by status (e.g., PENDING, PAID, CANCELLED). Optional.",
                            required = false,
                            example = "PENDING"
                    ),
                    @Parameter(
                            name = "page",
                            description = "The page number for pagination. Defaults to 0.",
                            required = false,
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            description = "The number of reminders per page. Defaults to 10.",
                            required = false,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Debt reminders retrieved successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Success with reminders retrieved",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Get debt reminders for creator success\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"content\": [\n" +
                                                    "      {\n" +
                                                    "        \"debt_reminder_id\": 25,\n" +
                                                    "        \"debt_account_number\": \"235543224\",\n" +
                                                    "        \"debt_name\": \"Bảo Ngọc\",\n" +
                                                    "        \"amount\": 256000.00,\n" +
                                                    "        \"message\": \"send me\",\n" +
                                                    "        \"status\": \"PENDING\",\n" +
                                                    "        \"created_time\": \"2025-01-09T08:00:35\"\n" +
                                                    "      },\n" +
                                                    "      {\n" +
                                                    "        \"debt_reminder_id\": 23,\n" +
                                                    "        \"debt_account_number\": \"235543224\",\n" +
                                                    "        \"debt_name\": \"Bảo Ngọc\",\n" +
                                                    "        \"amount\": 765000.00,\n" +
                                                    "        \"message\": \"Please pay me back!\",\n" +
                                                    "        \"status\": \"PENDING\",\n" +
                                                    "        \"created_time\": \"2025-01-09T07:44:24\"\n" +
                                                    "      }\n" +
                                                    "    ],\n" +
                                                    "    \"pageable\": {\n" +
                                                    "      \"pageNumber\": 0,\n" +
                                                    "      \"pageSize\": 10,\n" +
                                                    "      \"sort\": {\n" +
                                                    "        \"empty\": true,\n" +
                                                    "        \"sorted\": false,\n" +
                                                    "        \"unsorted\": true\n" +
                                                    "      },\n" +
                                                    "      \"offset\": 0,\n" +
                                                    "      \"paged\": true,\n" +
                                                    "      \"unpaged\": false\n" +
                                                    "    },\n" +
                                                    "    \"last\": true,\n" +
                                                    "    \"totalPages\": 1,\n" +
                                                    "    \"totalElements\": 8,\n" +
                                                    "    \"size\": 10,\n" +
                                                    "    \"number\": 0,\n" +
                                                    "    \"sort\": {\n" +
                                                    "      \"empty\": true,\n" +
                                                    "      \"sorted\": false,\n" +
                                                    "      \"unsorted\": true\n" +
                                                    "    },\n" +
                                                    "    \"first\": true,\n" +
                                                    "    \"numberOfElements\": 8,\n" +
                                                    "    \"empty\": false\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Creator account not found or no reminders found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Creator account not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Creator account not found\",\n" +
                                                    "  \"message\": \"The debt reminder was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/creator/{creatorAccountId}")
    @APIMessage("Get debt reminders for creator success")
    public ResponseEntity<Page<GetDebtReminderForCreatorResponse>> getDebtRemindersForCreator(@PathVariable Integer creatorAccountId,
                                                                                                           @RequestParam(required = false) DebtReminderStatus status,
                                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                                           @RequestParam(defaultValue = "10") int size) throws DebtReminderNotFoundException {
        Pageable pageable = PageRequest.of(page, size);

        Page<GetDebtReminderForCreatorResponse> reminders = debtReminderService.getDebtRemindersForCreator(creatorAccountId, status, pageable);

        return ResponseEntity.ok(reminders);
    }

    @Operation(
            summary = "Get debt reminders for a creator",
            description = "Retrieve a paginated list of debt reminders created for a specific creator account. Optionally filter by debt reminder status.",
            parameters = {
                    @Parameter(
                            name = "creatorAccountId",
                            description = "ID of the creator account",
                            required = true,
                            example = "4"
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filter reminders by status (e.g., PENDING, PAID, CANCELLED). Optional.",
                            required = false,
                            example = "CANCELLED"
                    ),
                    @Parameter(
                            name = "page",
                            description = "The page number for pagination. Defaults to 0.",
                            required = false,
                            example = "1"
                    ),
                    @Parameter(
                            name = "size",
                            description = "The number of reminders per page. Defaults to 10.",
                            required = false,
                            example = "5"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Debt reminders retrieved successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Success with reminders retrieved",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Get debt reminders for debtor success\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"content\": [\n" +
                                                    "      {\n" +
                                                    "        \"debt_reminder_id\": 13,\n" +
                                                    "        \"creator_account_id\": 3,\n" +
                                                    "        \"creator_account_number\": \"345562455\",\n" +
                                                    "        \"creator_name\": \"Quốc Khánh\",\n" +
                                                    "        \"amount\": 700000.00,\n" +
                                                    "        \"message\": \"Please pay me back!\",\n" +
                                                    "        \"status\": \"CANCELLED\",\n" +
                                                    "        \"created_time\": \"2024-12-22T20:04:35\"\n" +
                                                    "      },\n" +
                                                    "      {\n" +
                                                    "        \"debt_reminder_id\": 12,\n" +
                                                    "        \"creator_account_id\": 3,\n" +
                                                    "        \"creator_account_number\": \"345562455\",\n" +
                                                    "        \"creator_name\": \"Quốc Khánh\",\n" +
                                                    "        \"amount\": 500000.00,\n" +
                                                    "        \"message\": \"Please pay me back!\",\n" +
                                                    "        \"status\": \"CANCELLED\",\n" +
                                                    "        \"created_time\": \"2024-12-22T20:04:35\"\n" +
                                                    "      }\n" +
                                                    "    ],\n" +
                                                    "    \"pageable\": {\n" +
                                                    "      \"pageNumber\": 1,\n" +
                                                    "      \"pageSize\": 5,\n" +
                                                    "      \"sort\": {\n" +
                                                    "        \"empty\": true,\n" +
                                                    "        \"sorted\": false,\n" +
                                                    "        \"unsorted\": true\n" +
                                                    "      },\n" +
                                                    "      \"offset\": 5,\n" +
                                                    "      \"paged\": true,\n" +
                                                    "      \"unpaged\": false\n" +
                                                    "    },\n" +
                                                    "    \"last\": false,\n" +
                                                    "    \"totalPages\": 4,\n" +
                                                    "    \"totalElements\": 17,\n" +
                                                    "    \"size\": 5,\n" +
                                                    "    \"number\": 1,\n" +
                                                    "    \"sort\": {\n" +
                                                    "      \"empty\": true,\n" +
                                                    "      \"sorted\": false,\n" +
                                                    "      \"unsorted\": true\n" +
                                                    "    },\n" +
                                                    "    \"first\": false,\n" +
                                                    "    \"numberOfElements\": 5,\n" +
                                                    "    \"empty\": false\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Creator account not found or no reminders found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Creator account not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Debtor account not found\",\n" +
                                                    "  \"message\": \"The debt reminder was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/debtor/{debtorAccountId}")
    @APIMessage("Get debt reminders for debtor success")
    public ResponseEntity<Page<DebtReminder>> getDebtRemindersForDebtor(@PathVariable Integer debtorAccountId,
                                                                                     @RequestParam(required = false) DebtReminderStatus status,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) throws DebtReminderNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<DebtReminder> reminders = debtReminderService.getDebtRemindersForDebtor(debtorAccountId, status, pageable);

        return ResponseEntity.ok(reminders);
    }

    @Operation(
            summary = "Cancel a debt reminder",
            description = "Cancels a specific debt reminder given its ID, the requester's account ID, and an optional reason.",
            parameters = {
                    @Parameter(
                            name = "debtReminderId",
                            description = "The ID of the debt reminder to cancel",
                            required = true,
                            example = "24"
                    ),
                    @Parameter(
                            name = "requesterAccountId",
                            description = "The account ID of the user making the cancellation request",
                            required = true,
                            example = "3"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reason for cancelling the debt reminder",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body",
                                    value = "{\n" +
                                            "  \"reason\": \"Because I like\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Debt reminder cancelled successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Successful cancellation",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Cancel debt reminder success\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Debt reminder not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Debt reminder not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Debt reminder not found\",\n" +
                                                    "  \"message\": \"The debt reminder was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @PutMapping("/cancel/{debtReminderId}")
    @APIMessage("Cancel debt reminder success")
    public ResponseEntity<Void> cancelDebtReminder(@PathVariable Integer debtReminderId,
                                                   @RequestParam Integer requesterAccountId,
                                                   @RequestBody CancelDebtReminderRequest request) throws DebtReminderNotFoundException, IOException {
        debtReminderService.cancelDebtReminder(debtReminderId, request, requesterAccountId);

        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "Pay a debt reminder",
            description = "Confirms the payment of a specific debt reminder using the provided OTP and email.",
            parameters = {
                    @Parameter(
                            name = "debtReminderId",
                            description = "The ID of the debt reminder to be paid",
                            required = true,
                            example = "24"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details required for confirming the payment",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body",
                                    value = "{\n" +
                                            "  \"otp\": \"123456\",\n" +
                                            "  \"email\": \"user@example.com\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Debt reminder paid successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Successful payment",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Pay debt success!\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid OTP provided",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Invalid OTP",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"OTP is not valid\",\n" +
                                                    "  \"message\": \"The OTP was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/{debtReminderId}/pay")
    @APIMessage("Pay debt success!")
    public ResponseEntity<Void> payDebtReminder(@PathVariable Integer debtReminderId, @RequestBody ConfirmTransferRequest request) throws EmailNotFoundException, OTPNotFoundException, DebtReminderNotFoundException, InsufficientBalanceException {
        log.info("payDebtReminder");
        internalTransferService.confirmTransfer(request);
        debtReminderService.payDebtReminder(debtReminderId);

        return ResponseEntity.ok(null);
    }
}
