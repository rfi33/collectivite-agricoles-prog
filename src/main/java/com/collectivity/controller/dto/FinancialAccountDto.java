package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FinancialAccountDto {
    private String id;
    private String type;
    private Double amount;
    private String holderName;
    private String mobileBankingService;
    private String mobileNumber;
    private String bankName;
    private Integer bankCode;
    private Integer bankBranchCode;
    private Long bankAccountNumber;
    private Integer bankAccountKey;
}