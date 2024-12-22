package com.example.backend.service;

import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.Account;
import com.example.backend.model.DebtReminder;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.DebtReminderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DebtReminderService {

    private final DebtReminderRepository debtReminderRepository;
    private final AccountRepository accountRepository;

    public DebtReminderService(DebtReminderRepository debtReminderRepository, AccountRepository accountRepository) {
        this.debtReminderRepository = debtReminderRepository;
        this.accountRepository = accountRepository;
    }

    public DebtReminder createDebtReminder(Integer creatorAccountId, Integer debtorAccountId, BigDecimal amount, String message) {
        Account creatorAccount = accountRepository.findById(creatorAccountId)
                .orElseThrow(() -> new NotFoundException("Creator account not found"));

        Account debtorAccount = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new NotFoundException("Debtor account not found"));

        DebtReminder debtReminder = new DebtReminder();
        debtReminder.setCreatorAccount(creatorAccount);
        debtReminder.setDebtorAccount(debtorAccount);
        debtReminder.setAmount(amount);
        debtReminder.setMessage(message);
        debtReminder.setStatus(DebtReminderStatus.PENDING);
        debtReminder.setCreatedAt(LocalDateTime.now());
        debtReminder.setUpdatedAt(LocalDateTime.now());

        return debtReminderRepository.save(debtReminder);
    }

    public Page<GetDebtReminderForCreatorResponse> getDebtRemindersForCreator(Integer creatorAccountId, DebtReminderStatus status, Pageable pageable) {
        Account creatorAccount = accountRepository.findById(creatorAccountId)
                .orElseThrow(() -> new NotFoundException("Creator account not found"));

        if (status == null) {
            return debtReminderRepository.findByCreatorAccountId(creatorAccountId, pageable);
        }

        return debtReminderRepository.findByCreatorAccountIdAAndStatus(creatorAccountId, status, pageable);
    }

    public Page<DebtReminder> getDebtRemindersForDebtor(Integer debtorAccountId, Pageable pageable) {
        Account debtorAccount = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new NotFoundException("Debtor account not found"));

        return debtReminderRepository.findByDebtorAccountId(debtorAccountId, pageable);
    }

    public void cancelDebtReminder(Integer reminderId, String reason) {
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Debt reminder not found"));

        reminder.setStatus(DebtReminderStatus.CANCELLED);
        reminder.setUpdatedAt(LocalDateTime.now());

        debtReminderRepository.save(reminder);
    }
}

