package com.example.money_way_2.service;

import com.example.money_way_2.dto.response.VTPassApiResponse;
import com.example.money_way_2.dto.webhook.VTPassWebhookResponse;
import org.springframework.http.ResponseEntity;

public interface VTPassWebhookService {
    ResponseEntity<VTPassWebhookResponse> billsWebhookHandler(VTPassApiResponse vtPassApiResponse);
}

