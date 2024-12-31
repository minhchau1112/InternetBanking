package com.example.backend.controller;

import com.example.backend.dto.request.DebtReminderRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.enums.StatusCode;
import com.example.backend.model.DebtReminder;
import com.example.backend.service.DebtReminderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/debt-reminders")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    public DebtReminderController(DebtReminderService debtReminderService) {
        this.debtReminderService = debtReminderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DebtReminder>> createDebtReminder(@RequestBody DebtReminderRequest request) {
        DebtReminder reminder = debtReminderService.createDebtReminder(
                request.getCreatorAccountId(),
                request.getDebtorAccountId(),
                request.getAmount(),
                request.getMessage()
        );

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

    @PutMapping("/cancel/{reminderId}")
    public ResponseEntity<Void> cancelDebtReminder(@PathVariable Integer reminderId, @RequestParam String reason) {
        debtReminderService.cancelDebtReminder(reminderId, reason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}