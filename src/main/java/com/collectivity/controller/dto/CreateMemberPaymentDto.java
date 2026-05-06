package com.collectivity.controller.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateMemberPaymentDto {
    private Integer amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;
}