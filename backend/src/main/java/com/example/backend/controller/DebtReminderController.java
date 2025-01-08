package com.example.backend.controller;

import com.example.backend.dto.request.CancelDebtReminderRequest;
import com.example.backend.dto.request.ConfirmTransferRequest;
import com.example.backend.dto.request.DebtReminderRequest;
import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.exception.DebtReminderNotFoundException;
import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.exception.OTPNotFoundException;
import com.example.backend.model.DebtReminder;
import com.example.backend.service.DebtReminderService;
import com.example.backend.service.InternalTransferService;
import com.example.backend.utils.annotation.APIMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @APIMessage("Create debt reminder success")
    public ResponseEntity<DebtReminder> createDebtReminder(@RequestBody DebtReminderRequest request) throws DebtReminderNotFoundException {
        DebtReminder reminder = debtReminderService.createDebtReminder(
                request.getCreatorAccountId(),
                request.getDebtorAccountId(),
                request.getAmount(),
                request.getMessage()
        );

        notificationHandler.sendNotification("New debt reminder created: " + reminder.getMessage(), String.valueOf(request.getCreatorAccountId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(reminder);
    }


    @GetMapping("/creator/{creatorAccountId}")
    @APIMessage("Get debt reminders for creator success")
    public ResponseEntity<Page<GetDebtReminderForCreatorResponse>> getDebtRemindersForCreator(@PathVariable Integer creatorAccountId,
                                                                                                           @RequestParam(required = false) DebtReminderStatus status,
                                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                                           @RequestParam(defaultValue = "10") int size) throws DebtReminderNotFoundException {
        Pageable pageable = PageRequest.of(page, size);

        Page<GetDebtReminderForCreatorResponse> reminders = debtReminderService.getDebtRemindersForCreator(creatorAccountId, status, pageable);

        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/debtor/{debtorAccountId}")
    @APIMessage("Get debt reminders for debtor success")
    public ResponseEntity<Page<DebtReminder>> getDebtRemindersForDebtor(@PathVariable Integer debtorAccountId,
                                                                                     @RequestParam(required = false) DebtReminderStatus status,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) throws DebtReminderNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<DebtReminder> reminders = debtReminderService.getDebtRemindersForDebtor(debtorAccountId, status, pageable);

        return ResponseEntity.ok(reminders);
    }

    @PutMapping("/cancel/{debtReminderId}")
    @APIMessage("Cancel debt reminder success")
    public ResponseEntity<Void> cancelDebtReminder(@PathVariable Integer debtReminderId,
                                                   @RequestParam Integer requesterAccountId,
                                                   @RequestBody CancelDebtReminderRequest request) throws DebtReminderNotFoundException {
        debtReminderService.cancelDebtReminder(debtReminderId, request, requesterAccountId);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/{debtReminderId}/pay")
    @APIMessage("Pay debt success!")
    public ResponseEntity<Void> payDebtReminder(@PathVariable Integer debtReminderId, @RequestBody ConfirmTransferRequest request) throws EmailNotFoundException, OTPNotFoundException, DebtReminderNotFoundException {
        internalTransferService.confirmTransfer(request);
        debtReminderService.payDebtReminder(debtReminderId);

        return ResponseEntity.ok(null);
    }
}
