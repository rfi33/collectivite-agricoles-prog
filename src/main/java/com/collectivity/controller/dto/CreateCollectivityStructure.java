package com.collectivity.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateCollectivityStructure {
    private String president;
    private String vicePresident;
    private String treasurer;
    private String secretary;
}
