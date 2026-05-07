package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityTransactionDto {
    private String id;
    private LocalDate creationDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccountDto accountCredited;
    private MemberDto memberDebited;
}