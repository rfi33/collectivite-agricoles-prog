package com.collectivity.controller;

import com.collectivity.controller.dto.*;
import com.collectivity.controller.mapper.FinancialAccountDtoMapper;
import com.collectivity.controller.mapper.MemberDtoMapper;
import com.collectivity.entity.Member;
import com.collectivity.entity.MemberPayment;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.service.CollectivityService;
import com.collectivity.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CollectivityService collectivityService;
    private final MemberDtoMapper memberDtoMapper;
    private final FinancialAccountDtoMapper financialAccountDtoMapper;

    @PostMapping("/members")
    public ResponseEntity<?> createMembers(@RequestBody List<CreateMemberDto> dtos) {
        try {
            List<Member> entities = dtos.stream()
                    .map(memberDtoMapper::mapToEntity)
                    .toList();
            return ResponseEntity.status(CREATED).body(
                    memberService.addNewMembers(entities).stream()
                            .map(memberDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/members/{id}/payments")
    public ResponseEntity<?> createPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentDto> dtos) {
        try {
            List<CollectivityService.CreateMemberPaymentRequest> requests = dtos.stream()
                    .map(dto -> new CollectivityService.CreateMemberPaymentRequest(
                            dto.getAmount(),
                            dto.getMembershipFeeIdentifier(),
                            dto.getAccountCreditedIdentifier(),
                            dto.getPaymentMode() == null ? null
                                    : com.collectivity.entity.PaymentMode.valueOf(
                                            dto.getPaymentMode().name())
                    ))
                    .toList();

            List<MemberPayment> payments = collectivityService.createMemberPayments(id, requests);

            List<MemberPaymentDto> response = payments.stream()
                    .map(p -> MemberPaymentDto.builder()
                            .id(p.getId())
                            .amount(p.getAmount())
                            .paymentMode(p.getPaymentMode() == null ? null
                                    : PaymentMode.valueOf(p.getPaymentMode().name()))
                            .accountCredited(financialAccountDtoMapper.mapToDto(p.getAccountCredited()))
                            .creationDate(p.getCreationDate())
                            .build())
                    .toList();

            return ResponseEntity.status(CREATED).body(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}