package com.example.money_way_2.service.impl;

import com.example.money_way_2.dto.response.VTPassApiResponse;
import com.example.money_way_2.dto.webhook.VTPassWebhookResponse;
import com.example.money_way_2.enums.Status;
import com.example.money_way_2.model.Transaction;
import com.example.money_way_2.model.Wallet;
import com.example.money_way_2.repository.TransactionRepository;
import com.example.money_way_2.repository.WalletRepository;
import com.example.money_way_2.service.VTPassWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VTPassWebhookServiceImpl implements VTPassWebhookService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private String verifyAirtimeWebhook(VTPassApiResponse vtPassApiResponse) {
        String responseStatus = "failed";
        if (vtPassApiResponse.getContent().getTransactions().getStatus().equalsIgnoreCase("delivered")) {
            Transaction transaction = transactionRepository.findByTransactionId(Long.valueOf(vtPassApiResponse.getRequestId())).get();

            //update wallet if transaction is not already verified
            if (!transaction.getStatus().equals(Status.SUCCESS)) {
                Wallet userWallet = walletRepository.findByUserId(transaction.getUserId()).get();
                userWallet.setBalance(userWallet.getBalance().subtract(transaction.getAmount()));

                //update transaction status and save
                transaction.setStatus(Status.SUCCESS);
                transactionRepository.save(transaction);
            }

            //return success to webhook sender to stop sending events for this particular transaction
            responseStatus = "success";
        }

        return responseStatus;
    }

    @Override
    public ResponseEntity<VTPassWebhookResponse> billsWebhookHandler(VTPassApiResponse vtPassApiResponse) {

        //All implementations should return a 'success' string if transaction was successful according to VTPASS specification
        String responseStatus = null;

        //check if webhook is for Airtime transactions
        if (vtPassApiResponse.getContent().getTransactions().getType().equalsIgnoreCase("Airtime Recharge")) {
            responseStatus = verifyAirtimeWebhook(vtPassApiResponse);
        }

        return ResponseEntity.ok(VTPassWebhookResponse.builder()
                .response(responseStatus).build());
    }
}

