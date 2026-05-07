package com.collectivity.controller.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectivityDto extends CollectivityInformationDto {
    private String id;
    private String location;
    private CollectivityStructureDto structure;
    private List<MemberDto> members;
}