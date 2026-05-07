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
public class CreateCollectivityDto {
    private String location;
    private List<String> members;
    private Boolean federationApproval;
    private CreateCollectivityStructureDto structure;
}