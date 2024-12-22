package com.example.backend.repository;

import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.model.DebtReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, Integer> {
    @Query("""
        SELECT new com.example.backend.dto.response.GetDebtReminderForCreatorResponse(
            dr.debtorAccount.accountNumber,
            dr.debtorAccount.customer.name,
            dr.debtorAccount.customer.email,
            dr.amount,
            dr.message,
            dr.status,
            dr.createdAt
        )
        FROM DebtReminder dr
        WHERE dr.creatorAccount.id = :creatorAccountId
        ORDER BY dr.createdAt DESC
    """)
    Page<GetDebtReminderForCreatorResponse> findByCreatorAccountId(Integer creatorAccountId, Pageable pageable);
    @Query("""
        SELECT new com.example.backend.dto.response.GetDebtReminderForCreatorResponse(
            dr.debtorAccount.accountNumber,
            dr.debtorAccount.customer.name,
            dr.debtorAccount.customer.email,
            dr.amount,
            dr.message,
            dr.status,
            dr.createdAt
        )
        FROM DebtReminder dr
        WHERE dr.creatorAccount.id = :creatorAccountId AND dr.status = :debtReminderStatus
        ORDER BY dr.createdAt DESC
    """)
    Page<GetDebtReminderForCreatorResponse> findByCreatorAccountIdAAndStatus(Integer creatorAccountId, DebtReminderStatus debtReminderStatus, Pageable pageable);

    Page<DebtReminder> findByDebtorAccountId(Integer debtorAccountId, Pageable pageable);
}
