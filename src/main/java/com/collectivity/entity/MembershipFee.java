package com.collectivity.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode
public class MembershipFee {
    public String         id;
    public String         collectivityId;
    public LocalDate      eligibleFrom;
    public Frequency      frequency;
    public BigDecimal     amount;
    public String         label;
    public ActivityStatus status;
}