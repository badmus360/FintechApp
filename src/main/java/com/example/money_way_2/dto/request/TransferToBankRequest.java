package com.example.money_way_2.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferToBankRequest {
    private String account_bank;
    private String account_number;
    private BigDecimal amount;
    private String narration;
    private String currency;
    private String reference;
}
