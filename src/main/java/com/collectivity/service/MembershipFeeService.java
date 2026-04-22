package com.collectivity.service;

import com.collectivity.dto.request.CreateMembershipFeeRequest;
import com.collectivity.dto.response.MembershipFeeResponse;
import com.collectivity.entity.ActivityStatus;
import com.collectivity.entity.MembershipFee;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MembershipFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public List<MembershipFeeResponse> createAll(String collectivityId,
                                                 List<CreateMembershipFeeRequest> requests) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new NotFoundException("Collectivity not found: " + collectivityId);
        }

        List<MembershipFeeResponse> responses = new ArrayList<>();
        for (CreateMembershipFeeRequest request : requests) {
            responses.add(create(collectivityId, request));
        }
        return responses;
    }

    private MembershipFeeResponse create(String collectivityId, CreateMembershipFeeRequest request) {
        if (request.frequency == null) {
            throw new BadRequestException("Frequency is required and must be one of: WEEKLY, MONTHLY, ANNUALLY, PUNCTUALLY.");
        }

        if (request.amount == null || request.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Amount must be >= 0.");
        }

        MembershipFee fee = new MembershipFee();
        fee.collectivityId = collectivityId;
        fee.eligibleFrom   = request.eligibleFrom;
        fee.frequency      = request.frequency;
        fee.amount         = request.amount;
        fee.label          = request.label;
        fee.status         = ActivityStatus.ACTIVE;

        return toResponse(membershipFeeRepository.save(fee));
    }
    public MembershipFeeResponse toResponse(MembershipFee fee) {
        MembershipFeeResponse response = new MembershipFeeResponse();
        response.id           = fee.id;
        response.eligibleFrom = fee.eligibleFrom;
        response.frequency    = fee.frequency;
        response.amount       = fee.amount;
        response.label        = fee.label;
        response.status       = fee.status;
        return response;
    }
}