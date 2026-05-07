package com.collectivity.controller.mapper;

import com.collectivity.controller.dto.*;
import com.collectivity.entity.*;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CollectivityDtoMapper {

    private final MemberRepository memberRepository;
    private final MemberDtoMapper memberDtoMapper;

    public CollectivityDto mapToDto(Collectivity c) {
        CollectivityStructure s = c.getCollectivityStructure();
        return CollectivityDto.builder()
                .id(c.getId())
                .name(c.getName())
                .number(c.getNumber())
                .location(c.getLocation())
                .structure(s == null ? null : CollectivityStructureDto.builder()
                        .president(memberDtoMapper.mapToDto(s.getPresident()))
                        .vicePresident(memberDtoMapper.mapToDto(s.getVicePresident()))
                        .treasurer(memberDtoMapper.mapToDto(s.getTreasurer()))
                        .secretary(memberDtoMapper.mapToDto(s.getSecretary()))
                        .build())
                .members(c.getMembers() == null ? List.of()
                        : c.getMembers().stream().map(memberDtoMapper::mapToDto).toList())
                .build();
    }

    public Collectivity mapToEntity(CreateCollectivityDto dto) {
        CreateCollectivityStructureDto sd = dto.getStructure();

        CollectivityStructure structure = CollectivityStructure.builder()
                .president(findMember(sd.getPresident()))
                .vicePresident(findMember(sd.getVicePresident()))
                .treasurer(findMember(sd.getTreasurer()))
                .secretary(findMember(sd.getSecretary()))
                .build();

        List<Member> members = dto.getMembers() == null ? List.of() :
                dto.getMembers().stream().map(this::findMember).toList();

        return Collectivity.builder()
                .id(UUID.randomUUID().toString())
                .location(dto.getLocation())
                .federationApproval(dto.getFederationApproval())
                .collectivityStructure(structure)
                .members(members)
                .build();
    }

    private Member findMember(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member.id=" + id + " not found"));
    }
}