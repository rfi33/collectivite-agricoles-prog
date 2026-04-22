package com.collectivity.controller;

import com.collectivity.dto.request.CreateMemberPaymentRequest;
import com.collectivity.dto.request.CreateMemberRequest;
import com.collectivity.dto.response.MemberPaymentResponse;
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
    public ResponseEntity<List<MemberResponse>> create(
            @RequestBody List<CreateMemberRequest> requests) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.createAll(requests));
    }
    @PostMapping("/{id}/payments")
    public ResponseEntity<List<MemberPaymentResponse>> createPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentRequest> requests) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.createPayments(id, requests));
    }
}