package com.example.money_way_2.service;

import com.example.money_way_2.dto.request.AccountValidationDto;
import com.example.money_way_2.dto.request.CreateTransactionPinDto;
import com.example.money_way_2.dto.request.CreateWalletRequest;
import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.dto.response.ViewWalletResponseDto;
import com.example.money_way_2.dto.webhook.VerifyTransaction;
import com.example.money_way_2.dto.webhook.WebHookResponse;
import org.springframework.http.ResponseEntity;

public interface WalletService {

    ApiResponse createWallet(CreateWalletRequest request);

    ApiResponse createWallet(CreateWalletRequest request);

    ApiResponse<ViewWalletResponseDto> viewBalance();
    ApiResponse createWallet(CreateWalletRequest request);

    ApiResponse<VerifyTransaction> verifyPayment(String transactionId);

    ResponseEntity<String> processWebHookEvent(WebHookResponse<VerifyTransaction> webHookResponse);

    ApiResponse<AccountValidationDto> validateAccount(AccountValidationDto validate);
    ResponseEntity<ApiResponse>  updateWalletPin(CreateTransactionPinDto transactionPin);

}

