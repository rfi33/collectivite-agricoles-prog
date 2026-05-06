package com.collectivity.controller.mapper;

import com.collectivity.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FinancialAccountDtoMapper<financialAccount> {
    public FinancialAccount mapToDto(financialAccount, LocalDate at) {
        LocalDate balanceAt = at == null ? LocalDate.now() : at;
        if (financialAccount instanceof CashAccount cashAccount) {
            return edu.hei.school.agricultural.controller.dto.CashAccount.builder()
                    .id(cashAccount.getId())
                    .amount(cashAccount.getBalanceAt(balanceAt))
                    .build();
        } else if (financialAccount instanceof BankAccount bankAccount) {
            return edu.hei.school.agricultural.controller.dto.BankAccount.builder()
                    .id(bankAccount.getId())
                    .holderName(bankAccount.getHolderName())
                    .bankName(bankAccount.getBankName() == null ? null : Bank.valueOf(bankAccount.getBankName().name()))
                    .bankCode(bankAccount.getBankCode())
                    .bankBranchCode(bankAccount.getBranchCode())
                    .bankAccountNumber(bankAccount.getAccountNumber())
                    .bankAccountKey(bankAccount.getAccountKey())
                    .amount(bankAccount.getBalanceAt(balanceAt))
                    .build();
        } else if (financialAccount instanceof MobileBankingAccount mobileBankingAccount) {
            return edu.hei.school.agricultural.controller.dto.MobileBankingAccount.builder()
                    .id(mobileBankingAccount.getId())
                    .holderName(mobileBankingAccount.getHolderName())
                    .mobileNumber(mobileBankingAccount.getMobileNumber())
                    .mobileBankingService(mobileBankingAccount.getMobileBankingService() == null ? null : MobileBankingService.valueOf(mobileBankingAccount.getMobileBankingService().name()))
                    .amount(mobileBankingAccount.getBalanceAt(balanceAt))
                    .build();
        }
        throw new IllegalArgumentException("Unknown financial account type " + financialAccount.getClass().getName());
    }

}
