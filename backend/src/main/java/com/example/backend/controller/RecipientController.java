package com.example.backend.controller;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.dto.response.RecipientListResponse;
import com.example.backend.exception.CustomerNotFoundException;
import com.example.backend.model.LinkedBank;
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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
@Tag(name = "Recipient Controller")
public class RecipientController {

    @Autowired
    private RecipientService recipientService;

    @Autowired
    private LinkedBankRepository linkedBankRepository;

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
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Recipient fetched successfully.\",\n" +
                                                    "  \"data\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 1,\n" +
                                                    "      \"customer\": {\n" +
                                                    "        \"id\": 3,\n" +
                                                    "        \"username\": \"nlqkhanh\",\n" +
                                                    "        \"password\": \"$2a$10$0SsupHTuP8RS17QUbynsRuVcHnjmnQhqeiY08Mm2.fhbsMP99W/QS\",\n" +
                                                    "        \"createdAt\": \"2024-12-22T00:05:53\",\n" +
                                                    "        \"name\": \"Quốc Khánh\",\n" +
                                                    "        \"email\": \"chauhhcc@gmail.com\",\n" +
                                                    "        \"phone\": \"034724892\"\n" +
                                                    "      },\n" +
                                                    "      \"accountNumber\": \"984837497\",\n" +
                                                    "      \"aliasName\": \"Sơn làm frontend đẹp nhất\",\n" +
                                                    "      \"bankCode\": \"\",\n" +
                                                    "      \"createdAt\": \"2024-12-26T00:07:07\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{customer_id}")
    @APIMessage("Recipient fetched successfully.")
    public ResponseEntity<List<Recipient>> getRecipient
            (@PathVariable("customer_id") int customer_id) throws CustomerNotFoundException {

        List<Recipient> recipients = recipientService.findByCustomer(customer_id);
//        RecipientListResponse recipientListResponse = new RecipientListResponse(recipients);

        return ResponseEntity.ok(recipients);
    }

    @Operation(
            summary = "Create a new recipient",
            description = "This API creates a new recipient for the customer by providing customer details and recipient information.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recipient details to be created",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example request body with customerId exited amd accountNumber exited",
                                    value = "{\n" +
                                            "  \"customerId\": 3,\n" +
                                            "  \"accountNumber\": \"984837497\",\n" +
                                            "  \"aliasName\": \"Thanh Sơn\",\n" +
                                            "  \"bankCode\": \"\"\n" +
                                            "}"
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
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"Recipient created successfully.\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
            }
    )
    @PostMapping
    @APIMessage("Recipient created successfully.")
    public ResponseEntity<Void> createRecipient
            (@RequestBody @Valid RecipientCreateRequest createRequest) throws CustomerNotFoundException {

        if(!accountRepository.existsByAccountNumber(createRequest.getAccountNumber())) {
            throw new CustomerNotFoundException("Không tìm thấy khách hàng này");
        }

        try {
            Recipient recipient = recipientService.saveRecipient(createRequest);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }

    }

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

    @GetMapping("/banks")
    @APIMessage("Getted")
    public ResponseEntity<List<LinkedBank>> getBanks() {
        List<LinkedBank> banks = linkedBankRepository.findAll();
        return ResponseEntity.ok(banks);
    }
}
