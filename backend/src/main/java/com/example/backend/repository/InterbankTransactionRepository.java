package com.example.backend.repository;

import com.example.backend.model.InterbankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterbankTransactionRepository extends JpaRepository<InterbankTransaction, Integer> {

}
