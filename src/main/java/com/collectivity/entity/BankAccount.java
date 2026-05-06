package com.collectivity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankAccount extends FinancialAccount {
    private String holderName;
    private Bank bankName;
    private Integer bankCode;
    private Integer branchCode;
    private Integer accountNumber;
    private Integer accountKey;
}