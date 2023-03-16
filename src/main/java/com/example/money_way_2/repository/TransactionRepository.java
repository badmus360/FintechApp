package com.example.money_way_2.repository;

import com.example.money_way_2.model.Transaction;
import com.example.money_way_2.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transfer, Long> {
    Optional<Transaction> findByTransactionId(Long id);
}
