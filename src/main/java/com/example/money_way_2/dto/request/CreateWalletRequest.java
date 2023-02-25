package com.example.money_way_2.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateWalletRequest {
    private String email;
    private String bvn;
    private String currency;
    private BigDecimal amount;
    private String tx_ref;
    private String is_permanent;
    private String narration;
}
