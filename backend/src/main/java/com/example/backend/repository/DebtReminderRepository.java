package com.example.backend.repository;

import com.example.backend.model.DebtReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, Integer> {

    List<DebtReminder> findByCreatorAccountId(Integer creatorAccountId);

    List<DebtReminder> findByDebtorAccountId(Integer debtorAccountId);
}
