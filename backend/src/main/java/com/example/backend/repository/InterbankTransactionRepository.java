package com.example.backend.repository;

import com.example.backend.dto.response.InterbankTransactionResponse;
import com.example.backend.model.InterbankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterbankTransactionRepository extends JpaRepository<InterbankTransaction, Integer> {
    @Query("SELECT t FROM InterbankTransaction t " +
            "WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
            "AND (:partnerAccountNumber IS NULL OR t.externalAccountNumber = :partnerAccountNumber) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<InterbankTransaction> findInterbankTransactionsByAccount(
            Integer accountId,
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
