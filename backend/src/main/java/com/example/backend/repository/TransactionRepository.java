package com.example.backend.repository;

import com.example.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // Truy vấn theo source account id và destination account id (nếu có)
    @Query("SELECT t FROM Transaction t " +
            "WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
            "AND (:partnerAccountNumber IS NULL OR t.sourceAccount.accountNumber = :partnerAccountNumber OR t.destinationAccount.accountNumber = :partnerAccountNumber) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<Transaction> findAllTransactionsByAccount(
            Integer accountId,
            String partnerAccountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    Optional<Transaction> findFirstByStatus(String status);
    // Truy vấn theo source account id và destination account id (nếu có)
    @Query("SELECT t FROM Transaction t " +
            "WHERE (:accountNumber IS NULL OR t.sourceAccount.accountNumber = :accountNumber OR t.destinationAccount.accountNumber = :accountNumber) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<Transaction> findAllTransactionsByAccountNumber(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
