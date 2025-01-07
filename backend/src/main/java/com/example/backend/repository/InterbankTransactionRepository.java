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
    // Truy vấn theo source account id và destination account id (nếu có)
    @Query("SELECT t FROM InterbankTransaction t " +
            "LEFT JOIN LinkedBank b ON t.externalBankCode = b.bankCode " +
            "WHERE (:accountNumber IS NULL OR " +
            "t.sourceAccount.accountNumber = :accountNumber OR " +
            "t.destinationAccount.accountNumber = :accountNumber) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<InterbankTransaction> findInterbankTransactionsWithBankName(
            Integer sourceAccountId,
            String destinationAccountNum,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
