package com.collectivity.controller.dto;

import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCollectivityDto {
    private String location;
    private List<String> members;
    private Boolean federationApproval;
    private CreateCollectivityStructureDto structure;
}