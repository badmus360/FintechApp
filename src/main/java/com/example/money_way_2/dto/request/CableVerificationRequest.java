package com.example.money_way_2.dto.request;

import lombok.Data;

@Data
public class CableVerificationRequest {
    private Long billersCode;
    private String serviceID;
}
