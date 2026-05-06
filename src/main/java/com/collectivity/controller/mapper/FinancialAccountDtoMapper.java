package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.*;
import com.collectivity.entity.FinancialAccount;
import org.springframework.stereotype.Component;

@Component
public class FinancialAccountDtoMapper {

    public FinancialAccountDto mapToDto(FinancialAccount fa) {
        if (fa == null) return null;
        return switch (fa.getAccountType()) {
            case "CASH" -> CashAccountDto.builder()
                    .id(fa.getId())
                    .amount(fa.getAmount() == null ? 0.0 : fa.getAmount().doubleValue())
                    .build();
            case "MOBILE_BANKING" -> MobileBankingAccountDto.builder()
                    .id(fa.getId())
                    .holderName(fa.getHolderName())
                    .mobileNumber(fa.getMobileNumber())
                    .mobileBankingService(fa.getMobileMoney() == null ? null
                            : MobileBankingService.valueOf(fa.getMobileMoney()))
                    .amount(fa.getAmount() == null ? 0.0 : fa.getAmount().doubleValue())
                    .build();
            case "BANK" -> BankAccountDto.builder()
                    .id(fa.getId())
                    .holderName(fa.getHolderName())
                    .bankName(fa.getBankName() == null ? null : Bank.valueOf(fa.getBankName()))
                    .bankCode(fa.getBankCode())
                    .bankBranchCode(fa.getBankBranchCode())
                    .bankAccountNumber(fa.getBankAccountNumber())
                    .bankAccountKey(fa.getBankAccountKey())
                    .amount(fa.getAmount() == null ? 0.0 : fa.getAmount().doubleValue())
                    .build();
            default -> throw new IllegalArgumentException("Unknown account type: " + fa.getAccountType());
        };
    }
}