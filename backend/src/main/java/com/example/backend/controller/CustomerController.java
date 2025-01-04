package com.example.backend.controller;


import com.example.backend.dto.request.PasswordUpdateRequest;
import com.example.backend.model.Customer;
import com.example.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer")
    public Customer getCustomerDetailsByUsername(@RequestParam String username) {
        return customerService.getCustomerByUsername(username);
    }

    @PutMapping("/customer")
    public String updateCustomerPassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        boolean isUpdated = customerService.updatePassword(passwordUpdateRequest.getUsername(),
                passwordUpdateRequest.getPassword());

        if (isUpdated) {
            return "Password updated successfully";
        } else {
            throw new RuntimeException("Failed to update password");
        }
    }
}
