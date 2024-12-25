package com.example.backend.repository;

import com.example.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerId(Integer customerId);

    boolean existsById(Integer id);

    boolean existsByAccountNumber(String accountNumber);

}
