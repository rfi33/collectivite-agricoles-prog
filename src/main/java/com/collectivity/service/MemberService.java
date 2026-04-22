package com.collectivity.service;

<<<<<<< HEAD
import com.collectivity.dto.request.CreateMemberRequest;
import com.collectivity.dto.request.RefereeInfoRequest;
import com.collectivity.dto.response.MemberResponse;
import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

=======
import com.collectivity.entity.CreateMemberDTO;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.entity.Member;
import com.collectivity.entity.MemberOccupation;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
>>>>>>> f574a21c753cbd9cad632d47cfa12462e9cc5088
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
<<<<<<< HEAD

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
                    "The number of referees from the target collectivity must be >= the number of referees from other collectivities."
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
            response.referees = m.referees.stream()
                    .map(this::toResponse)
                    .toList();
        }

        return response;
=======
    private final CollectivityRepository collectivityRepository;

    public MemberService(MemberRepository memberRepository,
                         CollectivityRepository collectivityRepository) {
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    // ─── Public entry point ───────────────────────────────────────────────────

    public List<Member> createMembers(List<CreateMemberDTO> requests) {
        List<Member> result = new ArrayList<>();
        for (CreateMemberDTO request : requests) {
            try {
                result.add(createOne(request));
            } catch (SQLException e) {
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        }
        return result;
    }

    // ─── Single member admission ──────────────────────────────────────────────

    private Member createOne(CreateMemberDTO request) throws SQLException {

        // 1. Target collectivity must exist
        String collectivityId = request.getCollectivityIdentifier();
        if (collectivityId == null || collectivityId.isBlank()) {
            throw new BadRequestException("collectivityIdentifier is required.");
        }
        if (collectivityRepository.findById(collectivityId).isEmpty()) {
            throw new NotFoundException("Collectivity not found: " + collectivityId);
        }

        // 2. Payment conditions
        if (!Boolean.TRUE.equals(request.getRegistrationFeePaid())) {
            throw new BadRequestException("Registration fee (50 000 MGA) must be paid.");
        }
        if (!Boolean.TRUE.equals(request.getMembershipDuesPaid())) {
            throw new BadRequestException("Annual membership dues must be paid.");
        }

        // 3. B-2 referee rules
        List<String> refereeIds = request.getReferees();
        validateReferees(refereeIds, collectivityId);

        // 4. Build Member object
        Member member = new Member();
        member.setId(Long.valueOf(UUID.randomUUID().toString()));
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setBirthDate(request.getBirthDate());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());
        member.setProfession(request.getProfession());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setEmail(request.getEmail());
        member.setOccupation(request.getOccupation());

        // 5. Resolve referee IDs → full Member objects for the response
        List<Member> resolvedReferees = new ArrayList<>();
        for (String refereeId : refereeIds) {
            Member referee = memberRepository.findById(refereeId)
                    .orElseThrow(() -> new NotFoundException(
                            "Referee not found: " + refereeId));
            resolvedReferees.add(referee);
        }
        member.setReferees(resolvedReferees);

        // 6. Persist member + referee links in one transaction
        return memberRepository.save(member, refereeIds);
    }

    // ─── B-2 referee validation ───────────────────────────────────────────────

    /**
     * B-2 rules :
     * - At least 2 referees, all must be SENIOR (confirmed) members of the federation.
     * - Number of referees from the TARGET collectivity >= number from OTHER collectivities.
     *
     * Examples (target = collectivity #1) :
     *   2 from #1, 0 from others  → ✅  (2 >= 0)
     *   1 from #1, 1 from #2      → ✅  (1 >= 1)
     *   3 from #1, 2 from others  → ✅  (3 >= 2)
     *   0 from #1, 2 from others  → ❌  (0 < 2)
     */
    private void validateReferees(List<String> refereeIds,
                                  String targetCollectivityId) throws SQLException {

        if (refereeIds == null || refereeIds.size() < 2) {
            throw new BadRequestException(
                    "At least 2 confirmed (SENIOR) member referees are required.");
        }

        int fromTarget = 0;
        int fromOthers = 0;

        for (String refereeId : refereeIds) {

            Member referee = memberRepository.findById(refereeId)
                    .orElseThrow(() -> new NotFoundException(
                            "Referee not found: " + refereeId));

            // Must be a confirmed (SENIOR) member
            if (referee.getOccupation() != MemberOccupation.SENIOR) {
                throw new BadRequestException(
                        "Referee " + refereeId + " must be a confirmed (SENIOR) member.");
            }

            // Count by collectivity origin
            if (memberRepository.belongsToCollectivity(refereeId, targetCollectivityId)) {
                fromTarget++;
            } else {
                fromOthers++;
            }
        }

        // Core B-2 rule : referees from target >= referees from other collectivities
        if (fromTarget < fromOthers) {
            throw new BadRequestException(
                    "The number of referees from the target collectivity (" + fromTarget +
                            ") must be >= the number from other collectivities (" + fromOthers + ").");
        }
>>>>>>> f574a21c753cbd9cad632d47cfa12462e9cc5088
    }
}