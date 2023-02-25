package com.example.money_way_2.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class BanksResponse {
    private String status;
    private String message;
    private List<Map<String, String>> data;
}
