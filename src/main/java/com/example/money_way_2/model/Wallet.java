package com.example.money_way_2.model;

import lombok.*;

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
@Table(name = "wallet_tbl")
public class Wallet extends Base{
    private String bankName;
    private String accountNumber;
    private BigDecimal balance;
    private String virtualAccountRef;
    @Column(nullable = false)
    private Long userId;
}
