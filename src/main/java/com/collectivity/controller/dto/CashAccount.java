package com.collectivity.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CashAccount implements FinancialAccount {
    private String id;
    private Double amount;
}
