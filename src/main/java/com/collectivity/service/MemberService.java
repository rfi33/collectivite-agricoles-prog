package com.collectivity.service;

import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> addNewMembers(List<Member> members) {
        for (Member m : members) {
            if (Boolean.FALSE.equals(m.getRegistrationFeePaid())) {
                throw new BadRequestException("Registration fee not paid for member " + m.getFirstName());
            }
            if (Boolean.FALSE.equals(m.getMembershipDuesPaid())) {
                throw new BadRequestException("Membership dues not paid for member " + m.getFirstName());
            }
            if (!m.refereesAreEligible()) {
                throw new BadRequestException("Referees are not eligible for member " + m.getFirstName());
            }
            m.setId(UUID.randomUUID().toString());
        }
        return memberRepository.saveAll(members);
    }
}