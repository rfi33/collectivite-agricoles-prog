package com.collectivity.controller.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashAccountDto.class,         name = "CASH"),
        @JsonSubTypes.Type(value = MobileBankingAccountDto.class, name = "MOBILE_BANKING"),
        @JsonSubTypes.Type(value = BankAccountDto.class,          name = "BANK")
})
public interface FinancialAccountDto {
    String getId();
}