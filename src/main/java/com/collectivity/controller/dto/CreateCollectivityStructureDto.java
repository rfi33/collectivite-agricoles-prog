package com.collectivity.controller.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCollectivityStructureDto {
    private String president;
    private String vicePresident;
    private String treasurer;
    private String secretary;
}