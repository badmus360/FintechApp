package com.example.money_way_2.service.impl;

import com.example.money_way_2.dto.request.*;
import com.example.money_way_2.dto.response.*;
import com.example.money_way_2.enums.Status;
import com.example.money_way_2.exception.InsufficientFundsException;
import com.example.money_way_2.exception.InvalidCredentialsException;
import com.example.money_way_2.exception.ResourceNotFoundException;
import com.example.money_way_2.model.Beneficiary;
import com.example.money_way_2.model.Transaction;
import com.example.money_way_2.model.User;
import com.example.money_way_2.model.Wallet;
import com.example.money_way_2.repository.BeneficiaryRepository;
import com.example.money_way_2.repository.TransactionRepository;
import com.example.money_way_2.repository.WalletRepository;
import com.example.money_way_2.service.BillService;
import com.example.money_way_2.utils.AppUtil;
import com.example.money_way_2.utils.EnvironmentVariables;
import com.example.money_way_2.utils.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BillServiceImpl implements BillService {
    private final EnvironmentVariables environmentVariables;
    private final RestTemplate restTemplate;
    private final RestTemplateUtil restTemplateUtil;
    private final AppUtil appUtil;
    private final WalletRepository walletRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApiResponse buyData(DataPurchaseRequest request) {
        String transactionReference = appUtil.getReference()+"DATA-BUNDLE";
        DataRequestDto dataRequestDto = DataRequestDto.builder()
                .request_id(transactionReference)
                .serviceID(request.getServiceID())
                .billersCode(request.getBillersCode())
                .variation_code(request.getVariationCode())
                .amount(request.getAmount())
                .phone(request.getPhoneNumber())
                .build();

        User user = appUtil.getLoggedInUser();
        Long userId = user.getId();

        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new InvalidCredentialsException("Incorrect Pin");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Wallet Not Found"));
        BigDecimal walletBalance = wallet.getBalance();


        if (walletBalance.compareTo(request.getAmount()) >= 0){

            DataPurchaseResponse response = restTemplateUtil.getDataPurchaseResponse(dataRequestDto);

            if (request.isSaveBeneficiary()){
                saveBeneficiary(request, userId);
            }

            if (response.getCode().equals("000")){
                wallet.setBalance(walletBalance.subtract(request.getAmount()));
                walletRepository.save(wallet);
            }

            saveTransaction(response, userId);

            ApiResponse apiResponse = new ApiResponse<>();
            apiResponse.setStatus("SUCCESS");
            apiResponse.setMessage(response.getResponse_description());

            return apiResponse;
        }

        throw new InsufficientFundsException("Insufficient Funds");
    }

    @Override
    public AccountVerificationResponse verifyElectricityAccount(AccountVerificationRequest request) {
        HttpHeaders headers = restTemplateUtil.getVTPASS_Header();
        HttpEntity<AccountVerificationRequest> entity = new HttpEntity<>(request, headers);

        AccountVerificationResponse response = restTemplate.exchange(environmentVariables.getVerifyElectricityAccountUrl(),
                HttpMethod.POST, entity, AccountVerificationResponse.class).getBody();
        return response;
    }

    @Override
    public CableVerificationResponse verifyCableTv(CableVerificationRequest request) {
        HttpHeaders headers = restTemplateUtil.getVTPASS_Header();
        HttpEntity<CableVerificationRequest> entity =new HttpEntity<>(request, headers);

        CableVerificationResponse response= restTemplate.exchange(environmentVariables.getVerifyCableTvUrl(),
                HttpMethod.POST, entity, CableVerificationResponse.class).getBody();
        return response;
    }

    @Override
    public ApiResponse<TvPurchaseResponse> purchaseTvSubscription(CustomerRequestDtoForTvSubscription request) {
        User user = appUtil.getLoggedInUser();
        String transactionReference = appUtil.getReference() + "TV-SUBSCRIPTION";
        VtPassTvPurchaseRequest vtPassRequest = VtPassTvPurchaseRequest.builder()
                .phone(request.getPhone())
                .billersCode(request.getDecoderOrSmartCardNumber())
                .request_id(transactionReference)
                .subscription_type(request.getSubscriptionType())
                .serviceID(request.getDecoderName())
                .variation_code(request.getSubscriptionPackage())
                .build();

        Long userId = user.getId();
        if (!passwordEncoder.matches(request.getPin(),user.getPin())){
            throw new InvalidCredentialsException("Pin is incorrect");
        }
        Wallet wallet = walletRepository.findByUserId(appUtil.getLoggedInUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Wallet Not Found"));
        BigDecimal walletBalance = wallet.getBalance();
        if (walletBalance.compareTo(request.getAmount()) >= 0) {

            TvPurchaseResponse tvPurchaseResponse = restTemplateUtil.getTvPurchaseResponse(vtPassRequest);
            TvPurchaseResponseDto tvPurchaseResponseDto = new TvPurchaseResponseDto();
            BeanUtils.copyProperties(tvPurchaseResponse,tvPurchaseResponseDto);
            if (request.isSaveBeneficiary()) {
                saveBeneficiary(request, userId);
            }
            if (tvPurchaseResponse.getCode().equals("000")){
                wallet.setBalance(walletBalance.subtract(request.getAmount()));
                walletRepository.save(wallet);
            }

            saveTransactionForTvSubscription(tvPurchaseResponse,transactionReference,userId);
            return new ApiResponse<>(tvPurchaseResponse.getCode().equals("000") ? Status.SUCCESS.name():
                    Status.FAILED.name(), tvPurchaseResponseDto.getResponse_description(),null);
//                tvPurchaseResponse.getResponse_description()
        }
        return new ApiResponse<>("Failed","Insufficient Wallet Balance",null);

    }



    @Override
    public ApiResponse<DataVariationsResponse> fetchDataVariations(String dataServiceProvider) {
        DataVariationsResponse response = restTemplateUtil.fetchDataVariations(dataServiceProvider);
        return  new ApiResponse<>("Success", null, response);
    }

    @Override
    public VTPassResponse buyAirtime(AirtimeRequest airtimeRequest) {
        String requestId = appUtil.getReference()+"Airtime-"+appUtil.getLoggedInUser().getId();
        AirtimeRequestDto airtimeRequestDto = AirtimeRequestDto.builder()
                .request_id(requestId)
                .variation_code(airtimeRequest.getVariationCode())
                .serviceID(airtimeRequest.getServiceID())
                .phone(airtimeRequest.getPhoneNumber())
                .amount(airtimeRequest.getAmount())
                .billersCode(airtimeRequest.getBillersCode())
                .build();

        HttpEntity<AirtimeRequestDto> entity = new HttpEntity<>(airtimeRequestDto, restTemplateUtil.getVTPASS_Header());
        VTPassApiResponse vtPassApiResponse = restTemplate.exchange(environmentVariables.getBuy_airtime_endpoint(),
                HttpMethod.POST, entity, VTPassApiResponse.class).getBody();

        //Extracting the response payload
        VTPassApiContent vtPassApiContent = appUtil.getObjectMapper().convertValue(vtPassApiResponse.getContent(), VTPassApiContent.class);
        VTPassResponseDto vtPassResponseDto = appUtil.getObjectMapper().convertValue(vtPassApiContent.getTransactions(), VTPassResponseDto.class);

        //Update wallet only when transaction is successful
        if (vtPassResponseDto.getStatus().equalsIgnoreCase("delivered")) {
            updateWallet(appUtil.getLoggedInUser().getId(), vtPassResponseDto.getUnit_price());
        }
        saveTransaction(vtPassResponseDto, vtPassApiResponse.getRequestId());

        return VTPassResponse.builder()
                .productName(vtPassResponseDto.getProduct_name())
                .uniqueElement(vtPassResponseDto.getUnique_element())
                .unitPrice(vtPassResponseDto.getUnit_price())
                .email(vtPassResponseDto.getEmail())
                .phoneNumber(vtPassResponseDto.getPhone())
                .quantity(vtPassResponseDto.getQuantity())
                .status(vtPassResponseDto.getStatus())
                .transactionId(vtPassResponseDto.getTransactionId())
                .type(vtPassResponseDto.getType())
                .build();
    }

    @Override
    public ApiResponse payElectricityBill(ElectricityBillRequest request) {
        String requestId = appUtil.getReference();
        ElectricityRequestDto requestDto = ElectricityRequestDto.builder()
                .request_id(requestId)
                .serviceID(request.getServiceID())
                .billersCode(request.getBillersCode())
                .variation_code(request.getVariationCode())
                .amount(request.getAmount())
                .phone(request.getPhoneNumber())
                .build();

        User user = appUtil.getLoggedInUser();
        Long userId = user.getId();

        if (request.isSaveBeneficiary()){
            Optional<Beneficiary> savedBeneficiary = beneficiaryRepository.findByMeterNumber(request.getBillersCode());
            if(savedBeneficiary.isEmpty()){
                saveBeneficiary(request, userId);
            }
        }

        Wallet userWallet = walletRepository.findByUserId(userId).get();

        if (userWallet.getBalance().compareTo(BigDecimal.valueOf(Long.parseLong(request.getAmount()))) < 0){
            throw new InsufficientFundsException("Insufficient funds");
        }

        BillResponse billResponse = restTemplateUtil.getVTPassElectricityBillResponse(requestDto);

        if (billResponse.getCode().equals("000")){
            userWallet.setBalance(userWallet.getBalance().subtract(new BigDecimal(request.getAmount())));
            walletRepository.save(userWallet);
        }

        saveTransaction(requestId, userId, userWallet, billResponse);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(billResponse.getCode().equals("000") ? "SUCCESS" : "FAILED");
        apiResponse.setMessage(billResponse.getResponse_description());

        return apiResponse;
    }

    @Override
    public ApiResponse<TvVariationsResponse> fetchTvVariations(String tvServiceProvider) {
        TvVariationsResponse response = restTemplateUtil.fetchTvVariations(tvServiceProvider);
        return new ApiResponse<>("Success", null, response);

    }


    private void saveBeneficiary(DataPurchaseRequest request, Long userId) {
        Optional<Beneficiary> savedBeneficiary = beneficiaryRepository.findBeneficiariesByPhoneNumber(request.getPhoneNumber());
        if (savedBeneficiary.isEmpty()) {
            Beneficiary beneficiary = Beneficiary.builder()
                    .userId(userId)
                    .phoneNumber(request.getPhoneNumber()).build();
            beneficiaryRepository.save(beneficiary);
        }

    }

        private void saveTransactionForTvSubscription(TvPurchaseResponse response, String transactionReference, Long userId) {
            Transaction transaction = Transaction.builder()
                    .userId(userId).currency("NIL")
                    .request_id(transactionReference)
                    .amount(BigDecimal.valueOf(Double.parseDouble(response.getAmount())))
                    .status(response.getCode().equals("000") ? Status.valueOf("SUCCESS"): Status.valueOf("FAILED"))
                    .build();
            transactionRepository.save(transaction);
        }

        public AccountVerificationResponse verifyElectricityAccount(AccountVerificationRequest request) {
            HttpHeaders headers = restTemplateUtil.getVTPASS_Header();
            HttpEntity<AccountVerificationRequest> entity = new HttpEntity<>(request, headers);

            AccountVerificationResponse response = restTemplate.exchange(environmentVariables.getVerifyElectricityAccountUrl(),
                    HttpMethod.POST, entity, AccountVerificationResponse.class).getBody();
            return response;
        }

        private void updateWallet(Long userId, double amount) {
            Wallet userWallet = walletRepository.findByUserId(userId).get();
            userWallet.setBalance(userWallet.getBalance().subtract(BigDecimal.valueOf(amount)));

            walletRepository.save(userWallet);
        }

    private void saveTransaction(DataPurchaseResponse response, Long userId) {
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .currency("NGN")
                .status(response.getCode().equals("000") ? Status.valueOf("SUCCESS") : Status.valueOf("FAILED"))
                .request_id(response.getRequestId())
                .amount(BigDecimal.valueOf(Double.parseDouble(response.getAmount())))
                .build();

        transactionRepository.save(transaction);
    }

}
}
