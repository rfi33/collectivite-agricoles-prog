package edu.hei.school.agricultural.controller.mapper;

import edu.hei.school.agricultural.controller.dto.CollectivityTransaction;
import edu.hei.school.agricultural.controller.dto.PaymentMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionDtoMapper {
    private final FinancialAccountDtoMapper financialAccountDtoMapper;
    private final MemberDtoMapper memberDtoMapper;

    public CollectivityTransaction mapToDto(
            edu.hei.school.agricultural.entity.CollectivityTransaction collectivityTransaction) {
        return CollectivityTransaction.builder()
                .id(collectivityTransaction.getId())
                .amount(collectivityTransaction.getAmount())
                .creationDate(collectivityTransaction.getCreationDate())
                .paymentMode(collectivityTransaction.getPaymentMode() == null ? null
                        : PaymentMode.valueOf(collectivityTransaction.getPaymentMode().name()))
                .accountCredited(financialAccountDtoMapper.mapToDto(
                        collectivityTransaction.getAccountCredited(), null))
                .memberDebited(memberDtoMapper.mapToDto(collectivityTransaction.getMemberDebited()))
                .build();
    }
}