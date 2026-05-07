package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.FinancialAccountDto;
import com.collectivity.entity.FinancialAccount;
import org.springframework.stereotype.Component;

@Component
public class FinancialAccountDtoMapper {

    public FinancialAccountDto mapToDto(FinancialAccount fa) {
        if (fa == null) return null;
        return FinancialAccountDto.builder()
                .id(fa.getId())
                .type(fa.getAccountType())
                .amount(fa.getAmount() == null ? 0.0 : fa.getAmount().doubleValue())
                .holderName(fa.getHolderName())
                .mobileBankingService(fa.getMobileMoney())
                .mobileNumber(fa.getMobileNumber())
                .bankName(fa.getBankName())
                .bankCode(fa.getBankCode())
                .bankBranchCode(fa.getBankBranchCode())
                .bankAccountNumber(fa.getBankAccountNumber())
                .bankAccountKey(fa.getBankAccountKey())
                .build();
    }
}