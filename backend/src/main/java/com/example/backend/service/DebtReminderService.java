package com.example.backend.service;

import com.example.backend.dto.request.CancelDebtReminderRequest;
import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.exception.DebtReminderNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.model.Account;
import com.example.backend.model.DebtReminder;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.DebtReminderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DebtReminderService {

    private final DebtReminderRepository debtReminderRepository;
    private final AccountRepository accountRepository;

    private final SimpMessagingTemplate messagingTemplate;
    public DebtReminderService(DebtReminderRepository debtReminderRepository, AccountRepository accountRepository, SimpMessagingTemplate messagingTemplate) {
        this.debtReminderRepository = debtReminderRepository;
        this.accountRepository = accountRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public DebtReminder createDebtReminder(Integer creatorAccountId, Integer debtorAccountId, BigDecimal amount, String message) throws DebtReminderNotFoundException {
        Account creatorAccount = accountRepository.findById(creatorAccountId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Creator account not found"));

        Account debtorAccount = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Debtor account not found"));

        DebtReminder debtReminder = new DebtReminder();
        debtReminder.setCreatorAccount(creatorAccount);
        debtReminder.setDebtorAccount(debtorAccount);
        debtReminder.setAmount(amount);
        debtReminder.setMessage(message);
        debtReminder.setStatus(DebtReminderStatus.PENDING);
        debtReminder.setCreatedAt(LocalDateTime.now());
        debtReminder.setUpdatedAt(LocalDateTime.now());

        DebtReminder savedDebtReminder = debtReminderRepository.save(debtReminder);

        sendToUser(debtReminder.getDebtorAccount().getId(), debtReminder.getCreatorAccount().getCustomer().getName() + " vừa tạo 1 nhắc nợ cho bạn");
        return savedDebtReminder;
    }

    public Page<GetDebtReminderForCreatorResponse> getDebtRemindersForCreator(Integer creatorAccountId, DebtReminderStatus status, Pageable pageable) throws DebtReminderNotFoundException {
        Account creatorAccount = accountRepository.findById(creatorAccountId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Creator account not found"));

        if (status == null) {
            return debtReminderRepository.findByCreatorAccountId(creatorAccountId, pageable);
        }

        return debtReminderRepository.findByCreatorAccountIdAAndStatus(creatorAccountId, status, pageable);
    }

    public Page<DebtReminder> getDebtRemindersForDebtor(Integer debtorAccountId, DebtReminderStatus status, Pageable pageable) throws DebtReminderNotFoundException {
        Account debtorAccount = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Debtor account not found"));

        if (status == null) {
            return debtReminderRepository.findByDebtorAccountId(debtorAccountId, pageable);
        }

        return debtReminderRepository.findByDebtorAccountIdAAndStatus(debtorAccountId, status, pageable);
    }

    public void cancelDebtReminder(Integer debtReminderId, CancelDebtReminderRequest request, Integer requesterAccountId) throws DebtReminderNotFoundException, IOException {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Debt reminder not found"));

        if (!debtReminder.getCreatorAccount().getId().equals(requesterAccountId) && !debtReminder.getDebtorAccount().getId().equals(requesterAccountId)) {
            throw new UnauthorizedException("You are not authorized to cancel this debt reminder");
        }

        debtReminder.setStatus(DebtReminderStatus.CANCELLED);
        debtReminder.setUpdatedAt(LocalDateTime.now());

        debtReminderRepository.save(debtReminder);

        if (debtReminder.getCreatorAccount().getId().equals(requesterAccountId)) {
            // Nếu người huỷ là người tạo nhắc nợ => Gửi thông báo cho người nợ
            log.info("Người hủy là người tạo nhắc nợ => Gửi thông báo cho người nợ");
            sendToUser(debtReminder.getDebtorAccount().getId(), "Nhắc nợ từ " + debtReminder.getCreatorAccount().getCustomer().getName() + " đã bị hủy. Lý do: " + request.getCancellationReason());
        } else {
            // Nếu người huỷ là người nợ => Gửi thông báo cho người gửi
            log.info("Người huỷ là người khác => Gửi thông báo cho người gửi");
            sendToUser(debtReminder.getCreatorAccount().getId(), debtReminder.getDebtorAccount().getCustomer().getName() + " đã hủy 1 nhắc nợ mà bạn tạo. Lý do: " + request.getCancellationReason());
        }
    }
    public void sendToUser(Integer userId, String message) {
        log.info("sendToUser: " + userId);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", message);
    }
    public void payDebtReminder(Integer debtReminderId) throws DebtReminderNotFoundException {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new DebtReminderNotFoundException("Debt reminder not found"));

        debtReminder.setStatus(DebtReminderStatus.PAID);
        debtReminder.setUpdatedAt(LocalDateTime.now());

        debtReminderRepository.save(debtReminder);

        sendToUser(debtReminder.getCreatorAccount().getId(), debtReminder.getDebtorAccount().getCustomer().getName() + " đã thanh toán 1 nhắc nợ");
    }
}

