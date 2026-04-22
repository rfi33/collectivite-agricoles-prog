package com.collectivity.service;

import com.collectivity.dto.response.MembershipFeeResponse;
import com.collectivity.entity.MembershipFee;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MembershipFeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipFeeService {

    private final MembershipFeeRepository membershipFeeRepository;
    private final CollectivityRepository  collectivityRepository;

    public MembershipFeeService(MembershipFeeRepository membershipFeeRepository,
                                CollectivityRepository collectivityRepository) {
        this.membershipFeeRepository = membershipFeeRepository;
        this.collectivityRepository  = collectivityRepository;
    }

    public List<MembershipFeeResponse> findByCollectivityId(String collectivityId) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new NotFoundException("Collectivity not found: " + collectivityId);
        }

        return membershipFeeRepository.findByCollectivityId(collectivityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MembershipFeeResponse toResponse(MembershipFee fee) {
        MembershipFeeResponse response = new MembershipFeeResponse();
        response.id           = fee.id;
        response.eligibleFrom = fee.eligibleFrom;
        response.frequency    = fee.frequency;
        response.amount       = fee.amount;
        response.label        = fee.label;
        response.activityStatus = fee.status;
        return response;
    }
}