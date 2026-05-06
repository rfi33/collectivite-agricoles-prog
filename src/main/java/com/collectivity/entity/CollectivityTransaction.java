package com.collectivity.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private BigDecimal amount;
    private String paymentMode;
    private String accountCreditedId;
    private Member memberDebited;
    private String collectivityId;
}