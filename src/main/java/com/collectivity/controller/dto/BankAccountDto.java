package com.collectivity.controller.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class BankAccountDto implements FinancialAccountDto {
    private String id;
    private String holderName;
    private Bank bankName;
    private Integer bankCode;
    private Integer bankBranchCode;
    private Long bankAccountNumber;
    private Integer bankAccountKey;
    private Double amount;
}