package com.example.backend.controller;

import com.example.backend.dto.request.CancelDebtReminderRequest;
import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.DebtReminderRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.enums.StatusCode;
import com.example.backend.model.DebtReminder;
import com.example.backend.service.DebtReminderService;
import com.example.backend.service.InternalTransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/debt-reminders")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;
    private final NotificationHandler notificationHandler;
    private final InternalTransferService internalTransferService;

    public DebtReminderController(DebtReminderService debtReminderService, NotificationHandler notificationHandler, InternalTransferService internalTransferService) {
        this.debtReminderService = debtReminderService;
        this.notificationHandler = notificationHandler;
        this.internalTransferService = internalTransferService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DebtReminder>> createDebtReminder(@RequestBody DebtReminderRequest request) {
        DebtReminder reminder = debtReminderService.createDebtReminder(
                request.getCreatorAccountId(),
                request.getDebtorAccountId(),
                request.getAmount(),
                request.getMessage()
        );

        notificationHandler.sendNotification("New debt reminder created: " + reminder.getMessage(), String.valueOf(request.getCreatorAccountId()));

        ApiResponse<DebtReminder> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), reminder, "Create debt reminder success", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @GetMapping("/creator/{creatorAccountId}")
    public ResponseEntity<ApiResponse<Page<GetDebtReminderForCreatorResponse>>> getDebtRemindersForCreator(@PathVariable Integer creatorAccountId,
                                                                                                           @RequestParam(required = false) DebtReminderStatus status,
                                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<GetDebtReminderForCreatorResponse> reminders = debtReminderService.getDebtRemindersForCreator(creatorAccountId, status, pageable);

        ApiResponse<Page<GetDebtReminderForCreatorResponse>> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), reminders, "Get debt reminders for creator success", LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/debtor/{debtorAccountId}")
    public ResponseEntity<ApiResponse<Page<DebtReminder>>> getDebtRemindersForDebtor(@PathVariable Integer debtorAccountId,
                                                                                     @RequestParam(required = false) DebtReminderStatus status,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DebtReminder> reminders = debtReminderService.getDebtRemindersForDebtor(debtorAccountId, status, pageable);
        ApiResponse<Page<DebtReminder>> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), reminders, "Get debt reminders for debtor success", LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/cancel/{debtReminderId}")
    public ResponseEntity<ApiResponse<Void>> cancelDebtReminder(@PathVariable Integer debtReminderId,
                                                   @RequestParam Integer requesterAccountId,
                                                   @RequestBody CancelDebtReminderRequest request) {
        debtReminderService.cancelDebtReminder(debtReminderId, request, requesterAccountId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), null, "Cancel debt reminder success", LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{debtReminderId}/pay")
    public ResponseEntity<ApiResponse<Void>> payDebtReminder(@PathVariable Integer debtReminderId, @RequestBody ConfirmTransferRequest request) {
        internalTransferService.confirmTransfer(request);
        debtReminderService.payDebtReminder(debtReminderId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, StatusCode.SUCCESS.getCode(), null, "Pay debt success!", LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }
}
