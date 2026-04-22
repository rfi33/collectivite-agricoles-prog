package com.collectivity.dto.response;

import com.collectivity.entity.PaymentMode;

import java.time.LocalDate;

public class MemberPaymentResponse {
    public String                  id;
    public int                     amount;
    public PaymentMode             paymentMode;
    public FinancialAccountResponse accountCredited;
    public LocalDate               creationDate;
}