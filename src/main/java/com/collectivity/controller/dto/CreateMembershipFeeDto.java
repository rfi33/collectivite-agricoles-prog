package com.collectivity.controller.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class CreateMembershipFeeDto {
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private Double amount;
    private String label;
}