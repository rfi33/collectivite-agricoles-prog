package com.collectivity.dto.response;

import com.collectivity.entity.AccountType;
import com.collectivity.entity.Bank;
import com.collectivity.entity.MobileMoney;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialAccountResponse {
    public String      id;
    public AccountType accountType;
    public BigDecimal  amount;
    public BigDecimal  balanceAt;
    public LocalDate   at;
    public String      holderName;
    public Bank        bankName;
    public Integer     bankCode;
    public Integer     bankBranchCode;
    public Long        bankAccountNumber;
    public Integer     bankAccountKey;
    public MobileMoney mobileMoney;
    public Long        mobileNumber;
}