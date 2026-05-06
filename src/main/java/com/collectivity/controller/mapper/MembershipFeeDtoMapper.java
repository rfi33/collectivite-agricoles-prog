package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.*;
import com.collectivity.entity.MembershipFee;
import org.springframework.stereotype.Component;

@Component
public class MembershipFeeDtoMapper {

    public MembershipFeeDto mapToDto(MembershipFee f) {
        return MembershipFeeDto.builder()
                .id(f.getId())
                .label(f.getLabel())
                .amount(f.getAmount())
                .eligibleFrom(f.getEligibleFrom())
                .frequency(f.getFrequency() == null ? null
                        : Frequency.valueOf(f.getFrequency().name()))
                .status(f.getStatus() == null ? null
                        : ActivityStatus.valueOf(f.getStatus().name()))
                .build();
    }

    public MembershipFee mapToEntity(CreateMembershipFeeDto dto) {
        return MembershipFee.builder()
                .label(dto.getLabel())
                .amount(dto.getAmount())
                .eligibleFrom(dto.getEligibleFrom())
                .frequency(dto.getFrequency() == null ? null
                        : com.collectivity.entity.Frequency.valueOf(dto.getFrequency().name()))
                .build();
    }
}