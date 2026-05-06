package com.collectivity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Transaction {
    private String id;
    private TransactionType type;
    private Double amount;
    private LocalDate creationDate;
    private Member memberDebited;
}
