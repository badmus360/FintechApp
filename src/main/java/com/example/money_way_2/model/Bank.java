package com.example.money_way_2.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bank_list_tbl")
public class Bank extends Base{
    private String bankName;
    private String bankCode;
}
