package com.collectivity.controller.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityInformationDto {
    private String name;
    private Integer number;
}