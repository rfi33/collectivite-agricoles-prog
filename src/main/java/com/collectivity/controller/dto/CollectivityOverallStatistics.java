package com.collectivity.controller.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
public class CollectivityOverallStatistics {
    private CollectivityInformationDto collectivityInformation;
    private Integer newMembersNumber;
    private Double overallMemberCurrentDuePercentage;
}