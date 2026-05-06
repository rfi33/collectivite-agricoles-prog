package com.collectivity.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FinancialAccount {
    private String id;
    private String collectivityId;
    private String accountType;
    private BigDecimal amount;
    private String holderName;
    private String bankName;
    private Integer bankCode;
    private Integer bankBranchCode;
    private Long bankAccountNumber;
    private Integer bankAccountKey;
    private String mobileMoney;
    private Long mobileNumber;
}