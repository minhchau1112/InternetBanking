package com.example.backend.controller;


import com.example.backend.dto.request.PasswordUpdateRequest;
import com.example.backend.exception.CustomerNotFoundException;
import com.example.backend.model.Customer;
import com.example.backend.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @Operation(
            summary = "Get customer by ID",
            description = "Fetches the details of a customer by their unique ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "The ID of the customer to fetch",
                            required = true,
                            example = "3"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer found successfully",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Customer found",
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"error\": null,\n" +
                                                    "  \"message\": \"CALL API SUCCESS\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"id\": 3,\n" +
                                                    "    \"username\": \"nlqkhanh\",\n" +
                                                    "    \"password\": \"$2a$10$0SsupHTuP8RS17QUbynsRuVcHnjmnQhqeiY08Mm2.fhbsMP99W/QS\",\n" +
                                                    "    \"createdAt\": \"2024-12-22T00:05:53\",\n" +
                                                    "    \"name\": \"Quốc Khánh\",\n" +
                                                    "    \"email\": \"chauhhcc@gmail.com\",\n" +
                                                    "    \"phone\": \"034724892\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name = "Case: Customer not found",
                                            value = "{\n" +
                                                    "  \"status\": 404,\n" +
                                                    "  \"error\": \"Customer not found\",\n" +
                                                    "  \"message\": \"The customer was not found\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/api/customer/{id}")
    public Customer getCustomerById(@PathVariable Integer id) throws CustomerNotFoundException {
        log.info("getCustomerById");
        return customerService.getCustomerById(id);
    }
}
