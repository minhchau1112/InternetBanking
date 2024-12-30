package com.example.backend.repository;

import com.example.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // Truy vấn theo source account id và destination account id (nếu có)
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.id = :sourceAccountId" +
            " AND (:destinationAccountId IS NULL OR t.destinationAccount.id = :destinationAccountId)" +
            " AND (:startDate IS NULL OR t.createdAt >= :startDate)" +
            " AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<Transaction> findTransactions(
            Integer sourceAccountId,
            Integer destinationAccountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
