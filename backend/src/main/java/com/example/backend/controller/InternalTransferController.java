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

    @PostMapping
    @APIMessage("OTP has been sent to your email")
    public ResponseEntity<InitiateInternalTransferResponse> initiateInternalTransfer(@RequestBody InternalTransferRequest request) throws DebtReminderNotFoundException {
        System.out.println("initiateInternalTransfer");
        InitiateInternalTransferResponse initiateInternalTransferResponse = internalTransferService.initiateTransfer(request);

        return ResponseEntity.ok(initiateInternalTransferResponse);
    }

    @PostMapping("/confirm")
    @APIMessage("Transfer completed successfully!")
    public ResponseEntity<Void> confirmInternalTransfer(@RequestBody ConfirmTransferRequest request) throws EmailNotFoundException, OTPNotFoundException, InsufficientBalanceException {
        internalTransferService.confirmTransfer(request);

        return ResponseEntity.ok(null);
    }
}
