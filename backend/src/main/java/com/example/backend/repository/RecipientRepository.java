package com.example.backend.repository;

import com.example.backend.dto.response.GetRecipientsResponse;
import com.example.backend.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipientRepository extends JpaRepository<Recipient, Integer> {

    @Query("select r from Recipient r where r.customer.id = :id")
    List<Recipient> findByCustomerId(@Param("id") Integer id);

    @Query("""
            SELECT new com.example.backend.dto.response.GetRecipientsResponse (
                a.customer.name,
                r.accountNumber,
                r.aliasName,
                r.bankCode
            )
            FROM Recipient r JOIN Account a ON a.accountNumber = r.accountNumber
            WHERE r.customer.id = :id
            """)
    List<GetRecipientsResponse> getRecipientsByCustomerId(@Param("id") Integer id);
}
