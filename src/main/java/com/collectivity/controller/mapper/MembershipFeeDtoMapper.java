package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.CreateMembershipFee;
import com.collectivity.entity.ActivityStatus;
import com.collectivity.entity.Frequency;
import com.collectivity.entity.MembershipFee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipFeeDtoMapper {
    public MembershipFee mapToDto(edu.hei.school.agricultural.entity.MembershipFee membershipFee) {
        return MembershipFee.builder()
                .id(membershipFee.getId())
                .label(membershipFee.getLabel())
                .amount(membershipFee.getAmount())
                .frequency(membershipFee.getFrequency() == null ? null : Frequency.valueOf(membershipFee.getFrequency().name()))
                .status(membershipFee.getStatus() == null ? null : ActivityStatus.valueOf(membershipFee.getStatus().name()))
                .eligibleFrom(membershipFee.getEligibleFrom())
                .build();
    }

    public edu.hei.school.agricultural.entity.MembershipFee mapToEntity(CreateMembershipFee createMembershipFee) {
        return edu.hei.school.agricultural.entity.MembershipFee.builder()
                .label(createMembershipFee.getLabel())
                .amount(createMembershipFee.getAmount())
                .frequency(createMembershipFee.getFrequency() == null ? null : edu.hei.school.agricultural.entity.Frequency.valueOf(createMembershipFee.getFrequency().name()))
                .eligibleFrom(createMembershipFee.getEligibleFrom())
                .build();
    }
}
