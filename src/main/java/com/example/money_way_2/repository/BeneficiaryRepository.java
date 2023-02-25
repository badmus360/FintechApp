package com.example.money_way_2.repository;

import com.example.money_way_2.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByAccountNumberAfterAndUserId(String accountNumber, Long userId);
}
