package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateMembershipFeeDto {
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private Double amount;
    private String label;
}