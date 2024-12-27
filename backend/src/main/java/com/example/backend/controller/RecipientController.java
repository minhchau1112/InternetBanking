package com.example.backend.controller;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.model.ApiResponse;
import com.example.backend.model.Recipient;
import com.example.backend.service.RecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipientController {

    @Autowired
    private RecipientService recipientService;

    @GetMapping("/recipients/{customer_id}")
    public ResponseEntity<ApiResponse<List<Recipient>>> getRecipient(@PathVariable("customer_id") int customer_id) {
        if(!recipientService.customerExistsById(customer_id)) {
            ApiResponse<List<Recipient>> apiResponse =
                    new ApiResponse<>(404, "ACCOUNT_NOT_FOUND", "Account does not exist.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        List<Recipient> recipients = recipientService.findByCustomer(customer_id);
        ApiResponse<List<Recipient>> apiResponse =
                new ApiResponse<>(200, null, "Recipient fetched successfully.", recipients);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/recipients")
    public ResponseEntity<ApiResponse<Recipient>> createRecipient(@RequestBody RecipientCreateRequest createRequest) {

        System.out.println(createRequest.toString());
        if(!recipientService.customerExistsById(createRequest.getCustomerId())) {
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(500, "CUSTOMER_NOT_FOUND", "Customer not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        try {
            Recipient recipient = recipientService.saveRecipient(createRequest);
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(201, null, "Recipient created successfully.", recipient);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(500, "CREATION_FAILED", "Failed to create recipient.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }

    }

    @PutMapping("/recipients/{recipient_id}")
    public ResponseEntity<ApiResponse<Recipient>> updateRecipient(@PathVariable("recipient_id") Integer recipient_id, @RequestBody RecipientUpdateRequest updateRequest) {
        if(!recipientService.recipientExistsById(updateRequest.getRecipientId())) {
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(500, "RECIPIENT_NOT_FOUND", "Recipient not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        try{
            Recipient recipient = recipientService.updateRecipient(updateRequest);
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(201, null, "Recipient updated successfully.", recipient);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            ApiResponse<Recipient> apiResponse =
                    new ApiResponse<>(500, "UPDATING_FAILED", "Failed to update recipient.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }

    }

    @DeleteMapping("/recipients/{recipient_id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipient(@PathVariable("recipient_id") int recipient_id) {
        if(!recipientService.recipientExistsById(recipient_id)) {
            ApiResponse<Void> apiResponse =
                    new ApiResponse<>(500, "RECIPIENT_NOT_FOUND", "Recipient not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        try {
            recipientService.delete(recipient_id);
            ApiResponse<Void> apiResponse =
                    new ApiResponse<>(201, null, "Recipient deleted successfully.", null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            ApiResponse<Void> apiResponse =
                    new ApiResponse<>(500, "DELETION_FAILED", "Failed to delete recipient.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
