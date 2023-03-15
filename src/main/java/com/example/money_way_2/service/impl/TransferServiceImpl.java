package com.example.money_way_2.service.impl;

import com.example.money_way_2.dto.request.TransferToBankDto;
import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.dto.response.TransferFeeResponse;
import com.example.money_way_2.dto.response.TransferToBankResponse;
import com.example.money_way_2.exception.InvalidCredentialsException;
import com.example.money_way_2.exception.InvalidTransactionException;
import com.example.money_way_2.model.User;
import com.example.money_way_2.model.Wallet;
import com.example.money_way_2.repository.WalletRepository;
import com.example.money_way_2.service.TransferService;
import com.example.money_way_2.utils.AppUtil;
import com.example.money_way_2.utils.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AppUtil appUtil;
    private final WalletRepository walletRepository;
    private final RestTemplateUtil restTemplateUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApiResponse transferToBank(TransferToBankDto transferToBankDto) {

        User user = appUtil.getLoggedInUser();
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow();

        BigDecimal fee = getTransferFee(transferToBankDto.getAmount()).getData();
        BigDecimal total = transferToBankDto.getAmount().add(fee);
        
        //Checking if user balance is greater than amount + fee
        if (wallet.getBalance().compareTo(total) < 0) {
            throw new InvalidTransactionException("Insufficient wallet balance");
        }

        //Checking if user pin matches
        if (!passwordEncoder.matches(transferToBankDto.getPin(), user.getPin())){
            throw new InvalidCredentialsException("Incorrect Pin");
        }

        //Generating a reference code
        String ref = appUtil.generateReference();

        TransferToBankResponse transferToBankResponse = restTemplateUtil.transferToBankWithFlutterwave(
                transferToBankDto, ref
        );

        //checking if transfer not queued succ


        return null;
    }

    @Override
    public ApiResponse<BigDecimal> getTransferFee(BigDecimal amount) {

        TransferFeeResponse feeResponse = restTemplateUtil.getTransferFeeFromFlutterwave(amount);

        ApiResponse<BigDecimal> apiResponse = new ApiResponse<>();
        assert feeResponse != null;
        apiResponse.setStatus(feeResponse.getStatus());
        apiResponse.setMessage(feeResponse.getMessage());
        apiResponse.setData(BigDecimal.valueOf((double) feeResponse.getData().get(0).get("fee")));

        return null;
    }
}
