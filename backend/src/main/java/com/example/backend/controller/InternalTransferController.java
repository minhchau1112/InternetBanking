package com.example.backend.controller;

import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.InternalTransferRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.InitiateInternalTransferResponse;
import com.example.backend.enums.StatusCode;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.service.InternalTransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer/internal")
public class InternalTransferController {

    private final InternalTransferService internalTransferService;

    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InitiateInternalTransferResponse>> initiateInternalTransfer(@RequestBody InternalTransferRequest request) {
        System.out.println("initiateInternalTransfer");
        String otp = internalTransferService.initiateTransfer(request);
        InitiateInternalTransferResponse initiateInternalTransferResponse = new InitiateInternalTransferResponse(otp);
        ApiResponse<InitiateInternalTransferResponse> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), initiateInternalTransferResponse, "OTP has been sent to your email: " + otp, LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmInternalTransfer(@RequestBody ConfirmTransferRequest request) throws EmailNotFoundException, OTPNotFoundException {
        internalTransferService.confirmTransfer(request);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), null, "Transfer completed successfully!", LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }
}
