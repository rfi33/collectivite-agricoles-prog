package com.collectivity.controller;

import com.collectivity.dto.request.CreateMemberPaymentRequest;
import com.collectivity.dto.request.CreateMemberRequest;
import com.collectivity.dto.response.MemberResponse;
import com.collectivity.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<List<MemberResponse>> create(@RequestBody List<CreateMemberRequest> requests) {
        List<MemberResponse> responses = memberService.createAll(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
    @PostMapping("/{id}/payments")
    public ResponseEntity<Void> addPayment(
            @PathVariable String id,
            @RequestBody CreateMemberPaymentRequest request) {

        memberService.createPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}