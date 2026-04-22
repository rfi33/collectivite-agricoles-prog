package com.collectivity.entity;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private double amount;
    private String collectivityId;
    private String memberId;
    private String accountCreditedId;
    private PaymentMode paymentMode;
}