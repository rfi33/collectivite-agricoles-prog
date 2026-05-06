package com.collectivity.controller.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MemberPaymentDto {
    private String id;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccountDto accountCredited;
    private LocalDate creationDate;
}