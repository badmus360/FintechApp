package com.example.money_way_2.model;

import lombok.*;
import org.hibernate.annotations.Cache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "airtime_tbl")
public class Airtime extends Base{
    private BigDecimal amount;
    private String telco;
    private String phoneNumber;
    @Column(nullable = false)
    private Long userId;
}
