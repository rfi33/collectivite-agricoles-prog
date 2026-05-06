package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.*;
import com.collectivity.entity.CollectivityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionDtoMapper {

    private final FinancialAccountDtoMapper financialAccountDtoMapper;
    private final MemberDtoMapper memberDtoMapper;

    public CollectivityTransactionDto mapToDto(CollectivityTransaction tx) {
        return CollectivityTransactionDto.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .creationDate(tx.getCreationDate())
                .paymentMode(tx.getPaymentMode() == null ? null
                        : PaymentMode.valueOf(tx.getPaymentMode().name()))
                .accountCredited(financialAccountDtoMapper.mapToDto(tx.getAccountCredited()))
                .memberDebited(memberDtoMapper.mapToDto(tx.getMemberDebited()))
                .build();
    }
}