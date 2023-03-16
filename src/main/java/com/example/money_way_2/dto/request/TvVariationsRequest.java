package com.example.money_way_2.dto.request;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class TvVariationsRequest {
    @Enumerated(EnumType.STRING)
    private TvServiceProvider serviceProvider;
}
