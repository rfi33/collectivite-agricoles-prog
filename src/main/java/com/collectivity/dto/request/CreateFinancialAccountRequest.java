package com.collectivity.dto.request;

import com.collectivity.entity.AccountType;
import com.collectivity.entity.Bank;
import com.collectivity.entity.MobileMoney;

public class CreateFinancialAccountRequest {
    public AccountType          accountType;
    public String               holderName;
    public Bank                 bankName;
    public Integer              bankCode;
    public Integer              bankBranchCode;
    public Long                 bankAccountNumber;
    public Integer              bankAccountKey;
    public MobileMoney          mobileMoney;
    public Long                 mobileNumber;
}