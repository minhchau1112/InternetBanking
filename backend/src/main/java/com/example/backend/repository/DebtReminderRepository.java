package com.example.backend.repository;

import com.example.backend.model.DebtReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, Integer> {

    Page<DebtReminder> findByCreatorAccountId(Integer creatorAccountId, Pageable pageable);

    Page<DebtReminder> findByDebtorAccountId(Integer debtorAccountId, Pageable pageable);
}
