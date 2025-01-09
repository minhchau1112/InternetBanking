package com.example.backend.controller;

import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.InternalTransferRequest;
import com.example.backend.dto.response.InitiateInternalTransferResponse;
import com.example.backend.exception.DebtReminderNotFoundException;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.InsufficientBalanceException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.service.InternalTransferService;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer/internal")
public class InternalTransferController {

    private final InternalTransferService internalTransferService;

    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @Operation(
            summary = "Initiate an internal transfer",
            description = "Initiates a transfer between two accounts. Returns a response indicating the status of the transfer.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details required for initiating the internal transfer",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body",
                                    value = "{\n" +
                                            "  \"source_account_id\": 4,\n" +
                                            "  \"destination_account_id\": 3,\n" +
                                            "  \"amount\": 100000,\n" +
                                            "  \"message\": \"Transfer test\",\n" +
                                            "  \"fee_payer\": \"SENDER\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OTP has been sent to your email",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Successful Transfer",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"OTP has been sent to your email\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"otp\": \"020662\",\n" +
                                                    "    \"transaction_id\": 52\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Destination account not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Destination Account Not Found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Destination account not found\",\n" +
                                                    "  \"message\": \"The debt reminder was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @APIMessage("OTP has been sent to your email")
    public ResponseEntity<InitiateInternalTransferResponse> initiateInternalTransfer(@RequestBody InternalTransferRequest request) throws DebtReminderNotFoundException {
        System.out.println("initiateInternalTransfer");
        InitiateInternalTransferResponse initiateInternalTransferResponse = internalTransferService.initiateTransfer(request);

        return ResponseEntity.ok(initiateInternalTransferResponse);
    }

    @Operation(
            summary = "Confirm internal transfer",
            description = "Confirms the transfer by validating OTP and email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details required to confirm the internal transfer",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body",
                                    value = "{\n" +
                                            "  \"otp\": \"356170\",\n" +
                                            "  \"email\": \"tnmchau1211@gmail.com\",\n" +
                                            "  \"transactionId\": 54\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transfer completed successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Successful transfer",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Transfer completed successfully!\",\n" +
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
    @PostMapping("/confirm")
    @APIMessage("Transfer completed successfully!")
    public ResponseEntity<Void> confirmInternalTransfer(@RequestBody ConfirmTransferRequest request) throws EmailNotFoundException, OTPNotFoundException, InsufficientBalanceException {
        internalTransferService.confirmTransfer(request);

        return ResponseEntity.ok(null);
    }
}
