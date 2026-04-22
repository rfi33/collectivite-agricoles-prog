package com.collectivity.controller;

import com.collectivity.dto.request.CollectivityInformationRequest;
import com.collectivity.dto.request.CreateCollectivityRequest;
import com.collectivity.dto.request.CreateMembershipFeeRequest;
import com.collectivity.dto.response.CollectivityResponse;
import com.collectivity.dto.response.MembershipFeeResponse;
import com.collectivity.service.CollectivityService;
import com.collectivity.service.MembershipFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService  collectivityService;
    private final MembershipFeeService membershipFeeService;
    public CollectivityController(CollectivityService collectivityService,
                                  MembershipFeeService membershipFeeService) {
        this.collectivityService  = collectivityService;
        this.membershipFeeService = membershipFeeService;
    }
    @PostMapping
    public ResponseEntity<List<CollectivityResponse>> create(
            @RequestBody List<CreateCollectivityRequest> requests) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectivityService.createAll(requests));
    }
    @PutMapping("/{id}/informations")
    public ResponseEntity<CollectivityResponse> updateInformations(
            @PathVariable String id,
            @RequestBody CollectivityInformationRequest request) {
        return ResponseEntity.ok(collectivityService.updateInformations(id, request));
    }

    @GetMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFeeResponse>> getMembershipFees(
            @PathVariable String id) {
        return ResponseEntity.ok(membershipFeeService.findByCollectivityId(id));
    }

    @PostMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFeeResponse>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeRequest> requests) {
        return ResponseEntity.ok(membershipFeeService.createAll(id, requests));
    }
}