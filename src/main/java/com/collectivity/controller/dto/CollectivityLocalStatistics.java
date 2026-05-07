package com.collectivity.controller.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
public class CollectivityLocalStatistics {
    private MemberDescription memberDescription;
    private Double earnedAmount;
    private Double unpaidAmount;
}