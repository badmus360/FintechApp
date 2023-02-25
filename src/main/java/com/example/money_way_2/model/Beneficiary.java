package com.example.money_way_2.model;

import com.example.money_way_2.enums.TransactionType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "beneficiary_tbl")
public class Beneficiary extends Base{
    private String name;
    private String accountNumber;
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String bankName;
    @Column(nullable = false)
    private Long userId;
}
