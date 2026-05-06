package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.*;
import com.collectivity.entity.*;
import com.collectivity.entity.Collectivity;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberDtoMapper {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public Member mapToEntity(CreateMemberDto dto) {
        Collectivity collectivity = collectivityRepository.findById(dto.getCollectivityIdentifier())
                .orElseThrow(() -> new NotFoundException(
                        "Collectivity.id=" + dto.getCollectivityIdentifier() + " not found"));

        List<Member> referees = dto.getReferees() == null ? List.of() :
                dto.getReferees().stream()
                        .map(refId -> {
                            Member ref = memberRepository.findById(refId)
                                    .orElseThrow(() -> new NotFoundException("Member.id=" + refId + " not found"));
                            ref.addCollectivity(collectivity);
                            return ref;
                        })
                        .toList();

        Member member = Member.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender() == null ? null : Gender.valueOf(dto.getGender().name()))
                .occupation(dto.getOccupation() == null ? null
                        : MemberOccupation.valueOf(dto.getOccupation().name()))
                .address(dto.getAddress())
                .profession(dto.getProfession())
                .phoneNumber(dto.getPhoneNumber() == null ? null : String.valueOf(dto.getPhoneNumber()))
                .email(dto.getEmail())
                .registrationFeePaid(dto.getRegistrationFeePaid())
                .membershipDuesPaid(dto.getMembershipDuesPaid())
                .build();

        member.addCollectivity(collectivity);
        member.addReferees(referees);
        return member;
    }

    public MemberDto mapToDto(Member m) {
        if (m == null) return null;
        return MemberDto.builder()
                .id(m.getId())
                .firstName(m.getFirstName())
                .lastName(m.getLastName())
                .birthDate(m.getBirthDate())
                .address(m.getAddress())
                .profession(m.getProfession())
                .phoneNumber(m.getPhoneNumber())
                .email(m.getEmail())
                .gender(m.getGender() == null ? null
                        : com.collectivity.controller.dto.Gender.valueOf(m.getGender().name()))
                .occupation(m.getOccupation() == null ? null
                        : com.collectivity.controller.dto.MemberOccupation.valueOf(m.getOccupation().name()))
                .referees(m.getReferees() == null ? List.of()
                        : m.getReferees().stream().map(this::mapToDto).toList())
                .build();
    }
}