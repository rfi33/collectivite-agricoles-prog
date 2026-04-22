package com.collectivity.controller;

import com.collectivity.entity.CreateMemberDTO;
import com.collectivity.entity.Member;
import com.collectivity.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * POST /members
     *
     * Body    : List<CreateMember>  — member info + refereeIds + payment flags
     * Returns : List<Member>        — referees resolved as full Member objects
     *
     * 201 → created
     * 400 → bad referees (B-2) OR payment not done
     * 404 → collectivity or referee member not found
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Member> createMembers(@RequestBody List<CreateMemberDTO> requests) {
        return memberService.createMembers(requests);
    }
}