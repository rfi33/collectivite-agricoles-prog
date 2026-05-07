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
public class CollectivityStructureDto {
    private MemberDto president;
    private MemberDto vicePresident;
    private MemberDto treasurer;
    private MemberDto secretary;
}