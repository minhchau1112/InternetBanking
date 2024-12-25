package com.example.backend.repository;

import com.example.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsById(Integer id);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Integer customerId);
}
