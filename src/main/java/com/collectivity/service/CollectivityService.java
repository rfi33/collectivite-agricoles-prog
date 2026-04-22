package com.collectivity.service;

import com.collectivity.dto.request.CollectivityInformationRequest;
import com.collectivity.dto.request.CreateCollectivityRequest;
import com.collectivity.dto.response.CollectivityResponse;
import com.collectivity.dto.response.CollectivityStructureResponse;
import com.collectivity.dto.response.CollectivityTransactionResponse;
import com.collectivity.entity.Collectivity;
import com.collectivity.entity.CollectivityTransaction;
import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import com.collectivity.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository       memberRepository;
    private final MemberService          memberService;
    private final TransactionRepository  transactionRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               MemberService memberService,
                               TransactionRepository transactionRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository       = memberRepository;
        this.memberService          = memberService;
        this.transactionRepository  = transactionRepository;
    }

    public List<CollectivityResponse> createAll(List<CreateCollectivityRequest> requests) {
        List<CollectivityResponse> responses = new ArrayList<>();
        for (CreateCollectivityRequest request : requests) {
            responses.add(create(request));
        }
        return responses;
    }

    private CollectivityResponse create(CreateCollectivityRequest request) {
        if (request.federationApproval == null || !request.federationApproval) {
            throw new BadRequestException("Federation approval is required to open a collectivity.");
        }
        if (request.structure == null
                || request.structure.president     == null
                || request.structure.vicePresident == null
                || request.structure.treasurer     == null
                || request.structure.secretary     == null) {
            throw new BadRequestException("Collectivity structure is required (president, vicePresident, treasurer, secretary).");
        }
        if (request.members == null || request.members.size() < 10) {
            throw new BadRequestException("At least 10 members are required.");
        }
        int seniorCount = memberRepository.countMembersWithSixMonthsSeniority(request.members);
        if (seniorCount < 5) {
            throw new BadRequestException(
                    "At least 5 members must have seniority >= 6 months. Found: " + seniorCount);
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

    public CollectivityResponse updateInformations(String id, CollectivityInformationRequest request) {
        Collectivity existing = collectivityRepository.findById(id);
        if (existing == null) {
            throw new NotFoundException("Collectivity not found: " + id);
        }
        if (collectivityRepository.existsByNameOrNumberExcludingId(request.name, request.number, id)) {
            throw new BadRequestException("Name or number already used by another collectivity.");
        }
        Collectivity updated = collectivityRepository.updateInformations(id, request.name, request.number);
        updated.members = memberRepository.findByCollectivityId(updated.id);
        return toResponse(updated);
    }

    public List<CollectivityTransactionResponse> getTransactions(String id,
                                                                 LocalDate from,
                                                                 LocalDate to) {
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be before or equal to 'to' date.");
        }

        if (collectivityRepository.findById(id) == null) {
            throw new NotFoundException("Collectivity not found: " + id);
        }

        List<CollectivityTransaction> transactions = transactionRepository.findByPeriod(id, from, to);

        return transactions.stream().map(tx -> {
            CollectivityTransactionResponse res = new CollectivityTransactionResponse();
            res.id               = tx.getId();
            res.creationDate     = tx.getCreationDate();
            res.amount           = tx.getAmount();
            res.paymentMode      = tx.getPaymentMode();
            res.accountCreditedId = tx.getAccountCreditedId();
            Member member = memberRepository.findById(tx.getMemberId());
            res.memberDebited = member != null ? memberService.toResponse(member) : null;

            return res;
        }).toList();
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