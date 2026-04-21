package com.collectivity.controller;


import com.collectivity.entity.CreateMemberDTO;
import com.collectivity.entity.Member;
import com.collectivity.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    public List<Member> create(@RequestBody List<CreateMemberDTO> dtos) {
        return dtos.stream().map(service::create).toList();
    }
}