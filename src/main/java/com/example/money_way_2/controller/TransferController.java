package com.example.money_way_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final TransferService transferService;

    @GetMapping("/fee")
    public ResponseEntity<ApiResponse> getTransferFee(@RequestParam("amount") BigDecimal amount) {
        return ResponseEntity.ok(transferService.getTransferFee(amount));
    }

    @PostMapping("/bank")
    public ResponseEntity<ApiResponse> transferToBank(@RequestBody TransferToBankDto transferToBankDto) {
        return ResponseEntity.ok(transferService.transferToBank(transferToBankDto));
    }

    @PostMapping("/bank/response")
    public ResponseEntity<String> updateTransferToBankResponse(
            @RequestBody TransferToBankResponse transferToBankResponse) {
        transferService.updateTransferToBankResponse(transferToBankResponse);
        return ResponseEntity.ok("Transfer Response Updated");
    }

    @PostMapping("/local-transfer")
    public ApiResponse localTransfer(@Valid @RequestBody LocalTransferDto localTransfer)  {
        return transferService.localTransfer(localTransfer);
    }

}
