package com.collectivity.controller.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MembershipFeeDto extends CreateMembershipFeeDto {
    private String id;
    private ActivityStatus status;
}