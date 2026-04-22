package com.collectivity.dto.response;

import com.collectivity.entity.PaymentMode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CollectivityTransactionResponse {
    private String id;
    private LocalDate creationDate;
    private double amount;
    private String memberId;
    private String accountCreditedId;
    private PaymentMode paymentMode;
}