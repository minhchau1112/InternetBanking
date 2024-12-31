package com.example.backend.controller;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.dto.response.RecipientListResponse;
import com.example.backend.exception.CustomerNotFoundException;
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
    public ResponseEntity<RecipientListResponse> getRecipient(@PathVariable("customer_id") int customer_id) throws CustomerNotFoundException {

        List<Recipient> recipients = recipientService.findByCustomer(customer_id);
        RecipientListResponse recipientListResponse = new RecipientListResponse(recipients);

        return ResponseEntity.ok(recipientListResponse);
    }

    @PostMapping
    @APIMessage("Recipient created successfully.")
    public ResponseEntity<Void> createRecipient(@RequestBody RecipientCreateRequest createRequest) throws CustomerNotFoundException {

        if(!recipientService.customerExistsById(createRequest.getCustomerId())) {
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
    public ResponseEntity<Void> updateRecipient(@PathVariable("recipient_id") Integer recipient_id, @RequestBody RecipientUpdateRequest updateRequest) {

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
            recipientService.delete(recipient_id);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
