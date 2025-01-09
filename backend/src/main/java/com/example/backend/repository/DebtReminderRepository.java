package com.example.backend.repository;

import com.example.backend.dto.response.GetDebtReminderForCreatorResponse;
import com.example.backend.enums.DebtReminderStatus;
import com.example.backend.model.DebtReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, Integer> {
    @Query("""
        SELECT new com.example.backend.dto.response.GetDebtReminderForCreatorResponse(
            dr.id,
            dr.debtorAccount.accountNumber,
            dr.debtorAccount.customer.name,
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
            dr.id,
            dr.debtorAccount.accountNumber,
            dr.debtorAccount.customer.name,
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

    @Query("""
        SELECT new com.example.backend.dto.response.GetDebtReminderForDebtorResponse(
            dr.id,
            dr.creatorAccount.id,
            dr.creatorAccount.accountNumber,
            dr.creatorAccount.customer.name,
            dr.amount,
            dr.message,
            dr.status,
            dr.createdAt
        )
        FROM DebtReminder dr
        WHERE dr.debtorAccount.id = :debtorAccountId
        ORDER BY dr.createdAt DESC
    """)
    Page<DebtReminder> findByDebtorAccountId(Integer debtorAccountId, Pageable pageable);

    @Query("""
        SELECT new com.example.backend.dto.response.GetDebtReminderForDebtorResponse(
            dr.id,
            dr.creatorAccount.id,
            dr.creatorAccount.accountNumber,
            dr.creatorAccount.customer.name,
            dr.amount,
            dr.message,
            dr.status,
            dr.createdAt
        )
        FROM DebtReminder dr
        WHERE dr.debtorAccount.id = :debtorAccountId AND dr.status = :debtReminderStatus
        ORDER BY dr.createdAt DESC
    """)
    Page<DebtReminder> findByDebtorAccountIdAAndStatus(Integer debtorAccountId, DebtReminderStatus debtReminderStatus, Pageable pageable);
}
