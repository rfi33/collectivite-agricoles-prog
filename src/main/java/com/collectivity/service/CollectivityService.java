package com.collectivity.service;

import com.collectivity.dto.request.AssignIdentityRequest;
import com.collectivity.dto.request.CreateCollectivityRequest;
import com.collectivity.dto.response.CollectivityResponse;
import com.collectivity.dto.response.CollectivityStructureResponse;
import com.collectivity.entity.Collectivity;
import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.ForbiddenException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository       memberRepository;
    private final MemberService          memberService;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               MemberService memberService) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository       = memberRepository;
        this.memberService          = memberService;
    }

    public List<CollectivityResponse> createAll(List<CreateCollectivityRequest> requests) {
        List<CollectivityResponse> responses = new ArrayList<>();
        for (CreateCollectivityRequest request : requests) {
            responses.add(create(request));
        }
        return responses;
    }

    public CollectivityResponse assignIdentity(String id, AssignIdentityRequest request) {
        Collectivity collectivity = collectivityRepository.findById(id);
        if (collectivity == null) {
            throw new NotFoundException("Collectivity not found: " + id);
        }
        if (collectivity.name != null || collectivity.number != null) {
            throw new ForbiddenException("Identity already assigned and cannot be changed.");
        }
        if (collectivityRepository.existsByNameOrNumber(request.name, request.number)) {
            throw new BadRequestException("Name or number already exists.");
        }
        Collectivity updated = collectivityRepository.assignIdentity(id, request.name, request.number);
        return toResponse(updated);
    }

    private CollectivityResponse create(CreateCollectivityRequest request) {
        if (request.federationApproval == null || !request.federationApproval) {
            throw new BadRequestException("Federation approval is required to open a collectivity.");
        }
        if (request.structure == null
                || request.structure.president    == null
                || request.structure.vicePresident == null
                || request.structure.treasurer    == null
                || request.structure.secretary    == null) {
            throw new BadRequestException("Collectivity structure is required.");
        }
        if (request.members == null || request.members.size() < 10) {
            throw new BadRequestException("At least 10 members are required.");
        }
        int seniorCount = memberRepository.countMembersWithSixMonthsSeniority(request.members);
        if (seniorCount < 5) {
            throw new BadRequestException("At least 5 members must have seniority >= 6 months. Found: " + seniorCount);
        }
        Member president     = findMemberOrThrow(request.structure.president);
        Member vicePresident = findMemberOrThrow(request.structure.vicePresident);
        Member treasurer     = findMemberOrThrow(request.structure.treasurer);
        Member secretary     = findMemberOrThrow(request.structure.secretary);
        List<Member> members = new ArrayList<>();
        for (String memberId : request.members) {
            members.add(findMemberOrThrow(memberId));
        }
        Collectivity collectivity = new Collectivity();
        collectivity.location           = request.location;
        collectivity.federationApproval = request.federationApproval;
        collectivity.president          = president;
        collectivity.vicePresident      = vicePresident;
        collectivity.treasurer          = treasurer;
        collectivity.secretary          = secretary;
        collectivity.members            = members;

        Collectivity saved = collectivityRepository.save(collectivity);
        collectivityRepository.saveMembers(saved.id, request.members);
        return toResponse(saved);
    }

    private Member findMemberOrThrow(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        return member;
    }

    public CollectivityResponse toResponse(Collectivity c) {
        CollectivityResponse response = new CollectivityResponse();
        response.id       = c.id;
        response.name     = c.name;
        response.number   = c.number;
        response.location = c.location;

        CollectivityStructureResponse structure = new CollectivityStructureResponse();
        structure.president     = c.president     != null ? memberService.toResponse(c.president)     : null;
        structure.vicePresident = c.vicePresident != null ? memberService.toResponse(c.vicePresident) : null;
        structure.treasurer     = c.treasurer     != null ? memberService.toResponse(c.treasurer)     : null;
        structure.secretary     = c.secretary     != null ? memberService.toResponse(c.secretary)     : null;
        response.structure = structure;

        if (c.members != null) {
            response.members = c.members.stream().map(memberService::toResponse).toList();
        }
        return response;
    }
}