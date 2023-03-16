package com.example.money_way_2.service;


import com.example.money_way_2.dto.request.*;
import com.example.money_way_2.dto.response.*;

public interface BillService {
    ApiResponse<TvPurchaseResponse> purchaseTvSubscription(CustomerRequestDtoForTvSubscription tvPurchaseRequest);


    ApiResponse buyData(DataPurchaseRequest request);

    AccountVerificationResponse verifyElectricityAccount(AccountVerificationRequest request);

    CableVerificationResponse verifyCableTv(CableVerificationRequest request);

    ApiResponse<TvPurchaseResponse> purchaseTvSubscription(CustomerRequestDtoForTvSubscription request);

    ApiResponse<TvPurchaseResponse> purchaseTvSubscription(CustomerRequestDtoForTvSubscription request);

    ApiResponse<TvPurchaseResponse> purchaseTvSubscription(CustomerRequestDtoForTvSubscription request);

    ApiResponse<DataVariationsResponse> fetchDataVariations(String dataServiceProvider);

    VTPassResponse buyAirtime(AirtimeRequest airtimeRequest);
    ApiResponse payElectricityBill(ElectricityBillRequest request);

    ApiResponse<TvVariationsResponse> fetchTvVariations(String tvServiceProvider);
}
