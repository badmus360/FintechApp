package com.example.money_way_2.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonIgnorePropertie
public class DataVariationsResponse<T> {
    private String responseDescription;
    private T content;
}
