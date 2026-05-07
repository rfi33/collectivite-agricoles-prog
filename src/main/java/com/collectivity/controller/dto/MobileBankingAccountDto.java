package com.collectivity.controller.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class MobileBankingAccountDto implements FinancialAccountDto {
    private String id;
    private String holderName;
    private MobileBankingService mobileBankingService;
    private String mobileNumber;
    private Double amount;
}