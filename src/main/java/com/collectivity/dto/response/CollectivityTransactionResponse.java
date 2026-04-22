package com.collectivity.dto.response;

import com.collectivity.entity.PaymentMode;

import java.time.LocalDate;

public class CollectivityTransactionResponse {
    public String         id;
    public LocalDate      creationDate;
    public double         amount;
    public PaymentMode    paymentMode;
    public String         accountCreditedId;
    public MemberResponse memberDebited;
}