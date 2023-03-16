package com.example.money_way_2.service;

import com.example.money_way_2.model.Bank;
import org.springframework.data.domain.Page;

public interface BankService {

    Page<Bank> getAllBanks(int pageNo, int pageSize);

    void updateBankList();
}
