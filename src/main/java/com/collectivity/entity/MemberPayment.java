package com.collectivity.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MemberPayment {
    private String id;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private LocalDate creationDate;
}