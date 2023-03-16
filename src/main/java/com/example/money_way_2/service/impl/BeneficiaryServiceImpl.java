package com.example.money_way_2.service.impl;

import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.model.Beneficiary;
import com.example.money_way_2.model.User;
import com.example.money_way_2.repository.BeneficiaryRepository;
import com.example.money_way_2.service.BeneficiaryService;
import com.example.money_way_2.utils.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AppUtil appUtil;
    @Override
    public ApiResponse<List<Beneficiary>> getBeneficiaries(String transactionType) {
        User user = appUtil.getLoggedInUser();

        List<Beneficiary> beneficiaryList = beneficiaryRepository
                .findAllByUserIdAndTransactionType(user.getId(), transactionType);

        ApiResponse<List<Beneficiary>> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("SUCCESS");
        apiResponse.setMessage("Successfully Fetched Beneficiaries of User");
        apiResponse.setData(beneficiaryList);

        return apiResponse;
    }
}
