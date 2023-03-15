package com.example.money_way_2.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class EnvironmentVariables {

    @Value("")
    private String FLW_SECRET_KEY;

    @Value("")
    private String createWalletUrl;

    @Value("")
    private String getBankUrl;
    @Value("")
    private String getTransferFeeUrl;
    @Value("")
    private String getTransferToBankUrl;
    @Value("")
    private String getRetryTransferToBankUrl;

    @Value("")
    private String purchaseSubscriptionUrl;
    @Value("")
    private String purchaseDataUrl;
    @Value("")
    private String verifyTransactionEndpoint;
    @Value("")
    private String WEBHOOK_VERIFY_HASH;
    @Value("")
    private String fetchDataVariations;
    @Value("")
    private String verifyElectricityAccountUrl;
    @Value("")
    private String verifyCableTvUrl;
    @Value("")
    private String VTPASS_API_KEY;
    @Value("")
    private String VTPASS_Public_Key;
    @Value("")
    private String VTPASS_Secret_Key;
    @Value("")
    private String buy_airtime_endpoint;
    @Value("")
    private String electricityBillsUrl;
    @Value("")
    private String validateAccountUrl;
    @Value("")
    private String fetchTvVariations;
}
