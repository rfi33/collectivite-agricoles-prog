package edu.hei.school.agricultural.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectivityTransaction extends Transaction {
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private Member memberDebited;
}