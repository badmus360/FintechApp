package com.example.money_way_2.service;

import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.model.Beneficiary;

import java.util.List;

public interface BeneficiaryService {
    ApiResponse<List<Beneficiary>> getBeneficiaries(String transactionType);
}
