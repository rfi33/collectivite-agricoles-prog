package com.collectivity.dto.response;

import com.collectivity.entity.Frequency;
import com.collectivity.entity.ActivityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipFeeResponse {
    public String         id;
    public LocalDate      eligibleFrom;
    public Frequency      frequency;
    public BigDecimal     amount;
    public String         label;
    public ActivityStatus activityStatus;
}