package com.collectivity.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityStructureDto {
    private MemberDto president;
    private MemberDto vicePresident;
    private MemberDto treasurer;
    private MemberDto secretary;
}