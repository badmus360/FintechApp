package com.example.money_way_2.repository;

import com.example.money_way_2.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
 Optional<Bank> findByBankCode(String bankCode);
}
