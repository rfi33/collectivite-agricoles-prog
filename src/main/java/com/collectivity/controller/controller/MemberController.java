package com.collectivity.controller.controller;

import com.collectivity.entity.entity.Member;
import org.springframework.web.bind.annotation.*;
import com.collectivity.service.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service = new MemberService();

    @PostMapping
    public List<Member> create(@RequestBody List<Member> members,
                               @RequestParam String collectivityId) {

        return members.stream()
                .map(m -> service.create(m, collectivityId))
                .toList();
    }
}