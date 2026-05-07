package com.collectivity.controller.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class CashAccountDto implements FinancialAccountDto {
    private String id;
    private Double amount;
}