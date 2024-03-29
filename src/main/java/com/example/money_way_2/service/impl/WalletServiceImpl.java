package com.example.money_way_2.service.impl;

import com.example.money_way_2.dto.request.AccountValidationDto;
import com.example.money_way_2.dto.request.AccountValidationRequest;
import com.example.money_way_2.dto.request.CreateTransactionPinDto;
import com.example.money_way_2.dto.request.CreateWalletRequest;
import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.dto.response.CreateWalletResponse;
import com.example.money_way_2.dto.response.ViewWalletResponseDto;
import com.example.money_way_2.dto.webhook.VerifyTransaction;
import com.example.money_way_2.dto.webhook.WebHookResponse;
import com.example.money_way_2.enums.Status;
import com.example.money_way_2.exception.InvalidTransactionException;
import com.example.money_way_2.exception.ResourceNotFoundException;
import com.example.money_way_2.model.Transaction;
import com.example.money_way_2.model.User;
import com.example.money_way_2.model.Wallet;
import com.example.money_way_2.repository.TransactionRepository;
import com.example.money_way_2.repository.UserRepository;
import com.example.money_way_2.repository.WalletRepository;
import com.example.money_way_2.service.WalletService;
import com.example.money_way_2.utils.AppUtil;
import com.example.money_way_2.utils.EnvironmentVariables;
import com.example.money_way_2.utils.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {


    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final RestTemplate restTemplate;
    private final RestTemplateUtil restTemplateUtil;
    private final AppUtil appUtil;
    private final EnvironmentVariables environmentVariables;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse createWallet(CreateWalletRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + environmentVariables.getFLW_SECRET_KEY());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateWalletRequest> entity = new HttpEntity<>(request, headers);

        ApiResponse apiResponse = restTemplate.exchange(environmentVariables.getCreateWalletUrl(),
                HttpMethod.POST, entity, ApiResponse.class).getBody();


        CreateWalletResponse walletResponse;
        if (apiResponse != null && apiResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
            walletResponse = appUtil.getObjectMapper().convertValue(apiResponse.getData(), CreateWalletResponse.class);
            User user = userRepository.findByEmail(request.getEmail()).get();
            Wallet wallet = Wallet.builder()
                    .userId(user.getId())
                    .bankName(walletResponse.getBank_name())
                    .accountNumber(walletResponse.getAccount_number())
                    .balance(BigDecimal.valueOf(0.00))
                    .virtualAccountRef(request.getTx_ref())
                    .build();
            walletRepository.save(wallet);
        }else{
            throw new ResourceNotFoundException("Wallet Creation failed: An error has occurred");
        }

        return new ApiResponse("Success", "Wallet created successfully", null);
    }

    @Override
    public ApiResponse viewBalance() {

        ViewWalletResponseDto viewWalletResponseDto;


        User user = appUtil.getLoggedInUser();

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Wallet Not Found"));

        viewWalletResponseDto = ViewWalletResponseDto.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .build();

        return ApiResponse.builder()
                .status("SUCCESS")
                .data(viewWalletResponseDto)
                .build();
    }

    @Override
    public ApiResponse<VerifyTransaction> verifyPayment(String transactionId) {

        //Flutter end-point for verifying transactions
        String url = environmentVariables.getVerifyTransactionEndpoint()+transactionId+"/verify";

        //Package request headers into an entity since request does not have a body
        HttpEntity entity = new HttpEntity<>(restTemplateUtil.headersForFlutterwave());

        //Make the API call and get the response object from Flutter
        ApiResponse apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, ApiResponse.class).getBody();


        //Convert response java.lang.Object to an instance of VerifyTransaction class
        VerifyTransaction transactionResponseObject = null;
        if (apiResponse != null) {
            transactionResponseObject = appUtil.getObjectMapper().convertValue(apiResponse.getData(), VerifyTransaction.class);
        } else {
            throw new InvalidTransactionException("Transaction verification failed");
        }

        //Validate the transaction object returned from Flutter-wave
        validateTransactionAndUpdateWallet(transactionResponseObject);

        return ApiResponse.<VerifyTransaction>builder()
                .data(transactionResponseObject)
                .message(apiResponse.getMessage())
                .status(apiResponse.getStatus()).build();
    }

    @Override
    public ResponseEntity<String> processWebHookEvent(WebHookResponse<VerifyTransaction> webHookResponse) {

        //Confirm that the webhook event is coming from Flutter
        String flutterWaveSecurityHeader = httpServletRequest.getHeader("verify-hash");
        if (flutterWaveSecurityHeader != null && flutterWaveSecurityHeader.equals(environmentVariables.getWEBHOOK_VERIFY_HASH())) {

            //Confirm that the transaction sent from webhook is same with what Flutter have in their record
            verifyPayment(webHookResponse.getData().getId().toString());

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //If transaction is successfully processed,
        //return 200 status code to webhook sender to stop them from sending event for this transaction
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }

    private void  validateTransactionAndUpdateWallet(VerifyTransaction transactionResponseObject) {

        //Get the transaction if already logged or create a new transaction
        Transaction transaction = transactionRepository.findByTransactionId(transactionResponseObject.getId())
                .orElse(
                        Transaction.builder()
                                .transactionId(transactionResponseObject.getId())
                                .status(Status.PENDING)
                                .currency(transactionResponseObject.getCurrency())
                                .description(transactionResponseObject.getNarration())
                                .amount(transactionResponseObject.getAmount())
                                .virtualAccountRef(transactionResponseObject.getTx_ref())
                                .providerStatus(transactionResponseObject.getAuth_model())
                                .responseMessage(transactionResponseObject.getProcessor_response())
                                .paymentType(transactionResponseObject.getPayment_type())
                                .build());


        //If transaction gotten from the db had been confirmed already then return;
        if (transaction.getStatus().equals(Status.SUCCESS)) {
            return;
        }
        //Verify that the status of the transaction returned from flutter is successful
        if (transactionResponseObject.getStatus().equalsIgnoreCase(Status.SUCCESS.name())) {
            transaction.setStatus(Status.SUCCESS);
        }
        //Verify that the currency of the payment was the expected currency
        else if (!transactionResponseObject.getCurrency().equalsIgnoreCase(transaction.getCurrency())) {
            throw new InvalidTransactionException("Currency mismatch");
        }

        //Update/create transaction history
        transactionRepository.save(transaction);

        //Update wallet iff transaction was successful
        if (transaction.getStatus().equals(Status.SUCCESS)) {
            Wallet wallet = walletRepository.findByVirtualAccountRef(transactionResponseObject.getTx_ref())
                    .orElseThrow(() -> new InvalidTransactionException("Wallet not funded"));

            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));

            walletRepository.save(wallet);
        }
    }

    @Override
    public ApiResponse validateAccount(AccountValidationDto validate) {
        AccountValidationRequest validationRequest = AccountValidationRequest.builder()
                .account_bank(validate.getAccountBank())
                .account_number(validate.getAccountNumber())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + environmentVariables.getFLW_SECRET_KEY());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountValidationRequest> entity = new HttpEntity<>(validationRequest, headers);

        return restTemplate.exchange(environmentVariables.getValidateAccountUrl(),
                HttpMethod.POST, entity, ApiResponse.class).getBody();
    }

    public ResponseEntity<ApiResponse> updateWalletPin (CreateTransactionPinDto createTransactionPinDto) {
        Long userId = appUtil.getLoggedInUser().getId();
        Wallet userWallet = walletRepository.findByUserId(userId).get();

        if (userWallet != null) {
            userWallet.setPin(passwordEncoder.encode(createTransactionPinDto.getNewPin()));
            walletRepository.save(userWallet);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Wallet pin successfully changed", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Error", "Wallet not found", null));
        }
    }
}
