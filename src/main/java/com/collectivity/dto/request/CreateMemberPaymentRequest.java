package com.collectivity.dto.request;

import com.collectivity.entity.PaymentMode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateMemberPaymentRequest {
    private int amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;
}