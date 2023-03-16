package com.example.money_way_2.dto.response;

import lombok.Data;

@Data
public class CableVerificationResponse<T> {
    private String code;
    private T content;
}
