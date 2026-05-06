package com.collectivity.controller.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CollectivityTransactionDto {
    private String id;
    private LocalDate creationDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccountDto accountCredited;
    private MemberDto memberDebited;
}