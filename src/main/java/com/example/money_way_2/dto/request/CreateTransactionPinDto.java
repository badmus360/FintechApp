package com.example.money_way_2.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateTransactionPinDto {
    @NotNull
    private String oldPin;
    @NotNull
    private String newPin;
    @NotNull
    private String confirmPin;

}
