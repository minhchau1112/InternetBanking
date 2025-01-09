package com.example.backend.controller;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.dto.response.GetRecipientsResponse;
import com.example.backend.exception.CustomerNotFoundException;
import com.example.backend.model.Recipient;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.LinkedBankRepository;
import com.example.backend.service.RecipientService;
import com.example.backend.utils.annotation.APIMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/recipients")
@Tag(name = "Recipient Controller")
@Slf4j
public class RecipientController {

    @Autowired
    private RecipientService recipientService;

    @Autowired
    private AccountRepository accountRepository;

    @Operation(
            summary = "Get all recipients of the customer",
            description = "Return a list containing all existing recipients of the customer",
            parameters = {
                    @Parameter(name = "customer_id", description = "The ID of the customer", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "API called successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: API called successfully with 1 result",
                                            value = """
                                                    {
                                                      "status": 200,
                                                      "error": null,
                                                      "message": "Recipient fetched successfully.",
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "customer": {
                                                            "id": 15,
                                                            "username": "dinhhuy",
                                                            "password": "$2a$10$p0YNXb.KbbaJMHES3AGVBuqRHXcBFsP0LIxatm2DaWGNXxkO5xD..",
                                                            "createdAt": "2025-01-09T12:06:01.484986",
                                                            "name": "Dinh Huy",
                                                            "email": "tdhuy.03@gmail.com",
                                                            "phone": "0902999002"
                                                          },
                                                          "accountNumber": "439030014096",
                                                          "aliasName": "Quoc Khanh",
                                                          "bankCode": "GROUP2",
                                                          "createdAt": "2025-01-09T12:07:08.321691"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Error: Customer not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Customer not found",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "error": "Customer not found",
                                                      "message": "The customer was not found",
                                                      "data": null,
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{customer_id}")
    @APIMessage("Recipient fetched successfully.")
    public ResponseEntity<List<Recipient>> getRecipient
            (@PathVariable("customer_id") int customer_id) throws CustomerNotFoundException {

        if(!recipientService.customerExistsById(customer_id)) {
            throw new CustomerNotFoundException("Customer not found");
        }

        List<Recipient> recipients = recipientService.findByCustomer(customer_id);

        return ResponseEntity.ok(recipients);
    }
    @GetMapping("/v2/{customerId}")
    @APIMessage("Recipient fetched successfully")
    public ResponseEntity<List<GetRecipientsResponse>> getRecipientsByCustomerId(@PathVariable int customerId) throws CustomerNotFoundException {
        log.info("getRecipientsByCustomerId");
        if(!recipientService.customerExistsById(customerId)) {
            log.info("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }

        List<GetRecipientsResponse> recipients = recipientService.getRecipientsByCustomerId(customerId);
        log.info("getRecipientsByCustomerId success");

        return ResponseEntity.ok(recipients);
    }

    @Operation(
            summary = "Create a new recipient",
            description = "This API creates a new recipient for the customer by providing customer details and recipient information.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recipient details to be created",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body with customerId exited and accountNumber exited",
                                    value = """
                                            {
                                              "customerId": 3,
                                              "accountNumber": "984837497",
                                              "aliasName": "Thanh Sơn",
                                              "bankCode": "GROUP2"
                                            }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recipient created successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: API called successfully with new recipient created",
                                            value = """
                                                    {
                                                      "status": 200,
                                                      "error": null,
                                                      "message": "Recipient created successfully.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Error: Customer not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Customer not found",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "error": "Customer not found",
                                                      "message": "The customer was not found",
                                                      "data": null,
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Recipient created failed", content = @Content)
            }
    )
    @PostMapping
    @APIMessage("Recipient created successfully.")
    public ResponseEntity<Void> createRecipient
            (@RequestBody @Valid RecipientCreateRequest createRequest) throws CustomerNotFoundException {

        if(Objects.equals(createRequest.getBankCode(), "GROUP2") &&
        !accountRepository.existsByAccountNumber(createRequest.getAccountNumber())) {
            throw new CustomerNotFoundException("Không tìm thấy khách hàng này");
        }

        try {
            Recipient recipient = recipientService.saveRecipient(createRequest);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Operation(
            summary = "Update a recipient",
            description = "This API update a recipient data for the customer by providing recipient information.",
            parameters = {
                    @Parameter(name = "recipient_id", description = "The ID of the recipient", required = true, in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recipient details to be updated",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body with recipientId exited and accountNumber exited",
                                    value = """
                                            {
                                              "recipientId": 1,
                                              "accountNumber": "984837497",
                                              "aliasName": "Thanh Sơn",
                                              "bankCode": "GROUP2"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recipient updated successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: API called successfully with recipient updated",
                                            value = """
                                                    {
                                                      "status": 200,
                                                      "error": null,
                                                      "message": "Recipient updated successfully.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Recipient updated failed", content = @Content)
            }
    )
    @PutMapping("/{recipient_id}")
    @APIMessage("Recipient updated successfully.")
    public ResponseEntity<Void> updateRecipient(@PathVariable("recipient_id") Integer recipient_id,
                                                @RequestBody @Valid  RecipientUpdateRequest updateRequest) {
        try{
            Recipient recipient = recipientService.updateRecipient(updateRequest);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @Operation(
            summary = "Delete a recipient",
            description = "This API delete a recipient for the customer by providing recipient information.",
            parameters = {
                    @Parameter(name = "recipient_id", description = "The ID of the recipient", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recipient delete successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: API called successfully with recipient delete",
                                            value = """
                                                    {
                                                      "status": 200,
                                                      "error": null,
                                                      "message": "Recipient delete successfully.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Recipient deleted failed", content = @Content)
            }
    )
    @DeleteMapping("/{recipient_id}")
    @APIMessage("Recipient deleted successfully.")
    public ResponseEntity<Void> deleteRecipient(@PathVariable("recipient_id") int recipient_id) {

        try {
            recipientService.deleteRecipient(recipient_id);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
