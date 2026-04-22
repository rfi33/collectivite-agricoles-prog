package com.collectivity.service;

import com.collectivity.dto.request.RefereeInfoRequest;
import com.collectivity.dto.response.MemberResponse;
import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponse> createAll(List<CreateMemberRequest> requests) {
        List<MemberResponse> responses = new ArrayList<>();
        for (CreateMemberRequest request : requests) {
            responses.add(create(request));
        }
        return responses;
    }

    private MemberResponse create(CreateMemberRequest request) {

        if (request.collectivityIdentifier == null) {
            throw new BadRequestException("Collectivity identifier is required.");
        }
        if (Boolean.FALSE.equals(request.registrationFeePaid)) {
            throw new BadRequestException("Registration fee must be paid.");
        }
        if (Boolean.FALSE.equals(request.membershipDuesPaid)) {
            throw new BadRequestException("Membership dues must be paid.");
        }
        if (request.referees == null || request.referees.size() < 2) {
            throw new BadRequestException("At least 2 confirmed referees are required.");
        }

        int internalCount = 0;
        int externalCount = 0;

        for (RefereeInfoRequest refereeInfo : request.referees) {
            Member referee = memberRepository.findById(refereeInfo.memberId);
            if (referee == null) {
                throw new NotFoundException("Referee not found: " + refereeInfo.memberId);
            }
            if (!memberRepository.isConfirmedWithSeniority(refereeInfo.memberId)) {
                throw new BadRequestException(
                        "Referee " + refereeInfo.memberId + " is not a confirmed member with more than 90 days seniority."
                );
            }
            String refereeCollectivityId = memberRepository.getCollectivityIdOf(refereeInfo.memberId);
            if (request.collectivityIdentifier.equals(refereeCollectivityId)) {
                internalCount++;
            } else {
                externalCount++;
            }
        }

        if (internalCount < externalCount) {
            throw new BadRequestException(
                    "The number of referees from the target collectivity must be >= the number from other collectivities."
            );
        }

        Member member = new Member();
        member.firstName      = request.firstName;
        member.lastName       = request.lastName;
        member.birthDate      = request.birthDate;
        member.gender         = request.gender;
        member.address        = request.address;
        member.profession     = request.profession;
        member.phoneNumber    = request.phoneNumber;
        member.email          = request.email;
        member.occupation     = request.occupation;
        member.collectivityId = request.collectivityIdentifier;

        Member saved = memberRepository.save(member);

        List<String> refereeIds = new ArrayList<>();
        List<String> relations  = new ArrayList<>();
        for (RefereeInfoRequest refereeInfo : request.referees) {
            refereeIds.add(refereeInfo.memberId);
            relations.add(refereeInfo.relation);
        }
        memberRepository.saveReferees(saved.id, refereeIds, relations);
        saved.referees = memberRepository.findRefereesByMemberId(saved.id);

        return toResponse(saved);
    }

    public MemberResponse toResponse(Member m) {
        MemberResponse response = new MemberResponse();
        response.id          = m.id;
        response.firstName   = m.firstName;
        response.lastName    = m.lastName;
        response.birthDate   = m.birthDate;
        response.gender      = m.gender;
        response.address     = m.address;
        response.profession  = m.profession;
        response.phoneNumber = m.phoneNumber;
        response.email       = m.email;
        response.occupation  = m.occupation;
        if (m.referees != null) {
            response.referees = m.referees.stream().map(this::toResponse).toList();
        }
        return response;
    }
}