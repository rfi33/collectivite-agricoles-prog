package com.collectivity.dto.request;

import com.collectivity.entity.Frequency;

import java.math.BigDecimal;
import java.time.LocalDate;
public class CreateMembershipFeeRequest {
    public LocalDate  eligibleFrom;
    public Frequency  frequency;
    public BigDecimal amount;
    public String     label;
}