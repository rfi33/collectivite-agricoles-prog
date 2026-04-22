package com.collectivity.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FinancialAccount {
    public String      id;
    public String      collectivityId;
    public AccountType accountType;
    public BigDecimal  amount;
    public String  holderName;
    public Bank    bankName;
    public Integer bankCode;
    public Integer bankBranchCode;
    public Long    bankAccountNumber;
    public Integer bankAccountKey;
    public MobileMoney mobileMoney;
    public Long                 mobileNumber;
}