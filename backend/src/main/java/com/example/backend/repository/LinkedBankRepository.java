package com.example.backend.repository;

import com.example.backend.model.LinkedBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkedBankRepository extends JpaRepository<LinkedBank, Integer> {
    Optional<LinkedBank> findByBankCode(String bankCode);
    Optional<LinkedBank> findByBankCodeAndType(String bankCode, String type);
}
