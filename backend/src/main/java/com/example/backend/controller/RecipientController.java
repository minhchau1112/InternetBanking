package com.example.backend.controller;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.dto.response.RecipientListResponse;
import com.example.backend.model.Recipient;
import com.example.backend.service.RecipientService;
import com.example.backend.utils.annotation.APIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    @Autowired
    private RecipientService recipientService;

    @GetMapping("/{customer_id}")
    @APIMessage("Recipient fetched successfully.")
    public ResponseEntity<RecipientListResponse> getRecipient(@PathVariable("customer_id") int customer_id) {
//        if (!recipientService.customerExistsById(customer_id)) {
//            RestResponse<Object> res = new RestResponse<>();
//            res.setData(null);
//            return ResponseEntity.notFound().body(res);
//        }

        List<Recipient> recipients = recipientService.findByCustomer(customer_id);
        RecipientListResponse recipientListResponse = new RecipientListResponse(recipients);

        return ResponseEntity.ok(recipientListResponse);
    }

    @PostMapping("")
    @APIMessage("Recipient created successfully.")
    public ResponseEntity<Void> createRecipient(@RequestBody RecipientCreateRequest createRequest) {

//        if(!recipientService.customerExistsById(createRequest.getCustomerId())) {
//            ApiResponse<Recipient> apiResponse =
//                    new ApiResponse<>(500, "CUSTOMER_NOT_FOUND", "Customer not found.", null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }

//        try {
            Recipient recipient = recipientService.saveRecipient(createRequest);
//            ApiResponse<Recipient> apiResponse =
//                    new ApiResponse<>(201, null, "Recipient created successfully.", recipient);
            return ResponseEntity.ok(null);
//        } catch (Exception e) {
//            ApiResponse<Recipient> apiResponse =
//                    new ApiResponse<>(500, "CREATION_FAILED", "Failed to create recipient.", null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
//        }

    }

    @PutMapping("/{recipient_id}")
    @APIMessage("Recipient updated successfully.")
    public ResponseEntity<Void> updateRecipient(@PathVariable("recipient_id") Integer recipient_id, @RequestBody RecipientUpdateRequest updateRequest) {
//        if(!recipientService.recipientExistsById(updateRequest.getRecipientId())) {
//            ApiResponse<Recipient> apiResponse =
//                    new ApiResponse<>(500, "RECIPIENT_NOT_FOUND", "Recipient not found.", null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }

//        try{
            Recipient recipient = recipientService.updateRecipient(updateRequest);
            return ResponseEntity.ok().body(null);
//        } catch (Exception e) {
//            ApiResponse<Recipient> apiResponse =
//                    new ApiResponse<>(500, "UPDATING_FAILED", "Failed to update recipient.", null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
//        }

    }

    @DeleteMapping("/{recipient_id}")
    @APIMessage("Recipient deleted successfully.")
    public ResponseEntity<Void> deleteRecipient(@PathVariable("recipient_id") int recipient_id) {
//        if(!recipientService.recipientExistsById(recipient_id)) {
//            ApiResponse<Void> apiResponse =
//                    new ApiResponse<>(500, "RECIPIENT_NOT_FOUND", "Recipient not found.", null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }

//        try {
            recipientService.delete(recipient_id);
            return ResponseEntity.ok().body(null);
//        } catch (Exception e) {
//            ApiResponse<Void> apiResponse =
//                    new ApiResponse<>(500, "DELETION_FAILED", "Failed to delete recipient.", null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
//        }
    }
}
