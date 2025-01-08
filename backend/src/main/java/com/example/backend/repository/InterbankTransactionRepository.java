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

    // truy vấn các interbank transaction co destination account id va source account id = accountId
    @Query("SELECT t FROM InterbankTransaction t " +
            "LEFT JOIN LinkedBank b ON t.externalBankCode = b.bankCode " +
            "WHERE (t.destinationAccount.id = :accountId OR t.sourceAccount.id = :accountId) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<InterbankTransaction> findInterbankTransactionsWithAccountId(
            Integer accountId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT t FROM InterbankTransaction t " +
            "WHERE (t.sourceAccount.id = :id OR t.destinationAccount.id = :id) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<InterbankTransaction> findInterbankTransactionsByAccountNumber(
            Integer id,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
