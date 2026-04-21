package com.collectivity.service;

import com.collectivity.entity.CreateMemberDTO;
import com.collectivity.entity.Member;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository repo;

    public MemberService(MemberRepository repo) {
        this.repo = repo;
    }

    public Member create(CreateMemberDTO dto) {

        if (!dto.registrationFeePaid || !dto.membershipDuesPaid) {
            throw new RuntimeException("Payment not complete");
        }

        Member m = new Member();

        m.firstName = dto.firstName;
        m.lastName = dto.lastName;
        m.birthDate = dto.birthDate;
        m.gender = dto.gender;
        m.address = dto.address;
        m.profession = dto.profession;
        m.phoneNumber = dto.phoneNumber;
        m.email = dto.email;
        m.occupation = dto.occupation;

        m.collectivityId = dto.collectivityIdentifier;
        m.registrationFeePaid = dto.registrationFeePaid;
        m.membershipDuesPaid = dto.membershipDuesPaid;

        return repo.save(m);
    }
}