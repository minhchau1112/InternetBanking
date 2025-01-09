package com.example.backend.controller;

import com.example.backend.model.LinkedBank;
import com.example.backend.repository.LinkedBankRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class LinkedBankController {
    @Autowired
    private LinkedBankRepository linkedBankRepository;

    /**
     * Get all linked banks
     * @return List of linked banks
     */
    @Operation(summary = "Get all linked banks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of linked banks"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/linked")
    public ResponseEntity<List<LinkedBank>> getAllLinkedBanks() {
        List<LinkedBank> banks = linkedBankRepository.findAll();
        return ResponseEntity.ok(banks);
    }
}
