package com.example.money_way_2.service;

import com.example.money_way_2.dto.request.TransferToBankDto;
import com.example.money_way_2.dto.response.ApiResponse;

import java.math.BigDecimal;

public interface TransferService {
    ApiResponse transferToBank(TransferToBankDto transferToBankDto);

    ApiResponse getTransferFee(BigDecimal amount);

}
