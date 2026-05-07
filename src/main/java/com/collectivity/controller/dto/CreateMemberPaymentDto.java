package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateMemberPaymentDto {
    private Integer amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;
}