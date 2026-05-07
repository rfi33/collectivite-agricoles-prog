package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityOverallStatistics {
    private CollectivityInformationDto collectivityInformation;
    private Integer newMembersNumber;
    private Double overallMemberCurrentDuePercentage;
}