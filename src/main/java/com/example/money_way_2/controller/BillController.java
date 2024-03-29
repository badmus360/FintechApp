package com.example.money_way_2.controller;

import com.example.money_way_2.dto.request.*;
import com.example.money_way_2.dto.response.*;
import com.example.money_way_2.dto.webhook.VTPassWebhookResponse;
import com.example.money_way_2.service.BillService;
import com.example.money_way_2.service.VTPassWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/bills")
public class BillController {

    private final BillService billService;

    @PostMapping("/tvSubscription")
    public ResponseEntity<ApiResponse<TvPurchaseResponse>> purchaseTvSubscription(@RequestBody CustomerRequestDtoForTvSubscription request){
        return ResponseEntity.ok(billService.purchaseTvSubscription(request));
    }
    private final VTPassWebhookService vtPassWebhookService;
    @PostMapping("/verify-account")
    public ResponseEntity<AccountVerificationResponse> VerifyElectricityAccount(@RequestBody AccountVerificationRequest request){
        return ResponseEntity.ok(billService.verifyElectricityAccount(request));
    }

    @PostMapping("/verify-cabletv")
    public ResponseEntity<CableVerificationResponse> verifyCableTv(@RequestBody CableVerificationRequest request){
        return ResponseEntity.ok(billService.verifyCableTv(request));
    }

    @GetMapping("/data-Variations/{dataServiceProvider}")
    public ResponseEntity<ApiResponse<DataVariationsResponse>> getDataVariations(@PathVariable String dataServiceProvider) {
        return ResponseEntity.ok(billService.fetchDataVariations(dataServiceProvider));
    }

    @PostMapping("/buy-airtime")
    public ResponseEntity<VTPassResponse> buyAirtime(AirtimeRequest airtimeRequest) {
        return ResponseEntity.ok(billService.buyAirtime(airtimeRequest));
    }

    @PostMapping("/buy-data")
    public ResponseEntity<ApiResponse> buyData(@Valid @RequestBody DataPurchaseRequest request){
        return ResponseEntity.ok(billService.buyData(request));
    }

    @PostMapping("/purchase-EKEDC-electricity")
    public ResponseEntity<ApiResponse> purchaseElectricityEKEDC(@RequestBody ElectricityBillRequest electricityRequest) {
        ApiResponse response = billService.payElectricityBill(electricityRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/bills-webhook-vtpass")
    public ResponseEntity<VTPassWebhookResponse> processWebHook(@RequestBody VTPassApiResponse vtPassApiResponse) {
        return vtPassWebhookService.billsWebhookHandler(vtPassApiResponse);
    }

    @GetMapping("/tv-variations/{tvServiceProvider}")
    public ResponseEntity<ApiResponse<TvVariationsResponse>> getTvVariations(@PathVariable String tvServiceProvider) {
        return ResponseEntity.ok(billService.fetchTvVariations(tvServiceProvider));
    }
}
