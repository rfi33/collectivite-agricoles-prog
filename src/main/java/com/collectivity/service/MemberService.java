package com.collectivity.service;

import com.collectivity.entity.Member;
import com.collectivity.repository.MemberRepository;

import java.util.List;

public class MemberService {

    private final MemberRepository repo = new MemberRepository();

    public Member create(Member m, String collectivityId) {

        if (!m.registrationFeePaid || !m.membershipDuesPaid) {
            throw new RuntimeException("Payment missing");
        }

        return repo.save(m, collectivityId);
    }

    public List<Member> getByCollectivity(String id) {
        return repo.findByCollectivity(id);
    }
}