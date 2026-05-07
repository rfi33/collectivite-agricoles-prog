package com.collectivity.controller.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityDto {
    private String id;
    private String name;
    private Integer number;
    private String location;
    private CollectivityStructureDto structure;
    private List<MemberDto> members;
}