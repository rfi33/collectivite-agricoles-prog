package com.collectivity.controller;

import com.collectivity.controller.dto.*;
import com.collectivity.controller.mapper.*;
import com.collectivity.entity.Collectivity;
import com.collectivity.entity.MembershipFee;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.service.CollectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class CollectivityController {

    private final CollectivityService collectivityService;
    private final CollectivityDtoMapper collectivityDtoMapper;
    private final MembershipFeeDtoMapper membershipFeeDtoMapper;
    private final FinancialAccountDtoMapper financialAccountDtoMapper;
    private final TransactionDtoMapper transactionDtoMapper;

    @GetMapping("/collectivities/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    collectivityDtoMapper.mapToDto(collectivityService.getCollectivityById(id)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/collectivities")
    public ResponseEntity<?> create(@RequestBody List<CreateCollectivityDto> dtos) {
        try {
            List<Collectivity> entities = dtos.stream()
                    .map(collectivityDtoMapper::mapToEntity)
                    .toList();
            return ResponseEntity.status(CREATED).body(
                    collectivityService.createCollectivities(entities).stream()
                            .map(collectivityDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/collectivities/{id}/informations")
    public ResponseEntity<?> updateInformations(
            @PathVariable String id,
            @RequestBody CollectivityInformationDto dto) {
        try {
            return ResponseEntity.ok(collectivityDtoMapper.mapToDto(
                    collectivityService.updateInformations(id, dto.getName(), dto.getNumber())));
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> getMembershipFees(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    collectivityService.getMembershipFeesByCollectivityId(id).stream()
                            .map(membershipFeeDtoMapper::mapToDto)
                            .toList());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeDto> dtos) {
        try {
            List<MembershipFee> fees = dtos.stream()
                    .map(membershipFeeDtoMapper::mapToEntity)
                    .toList();
            return ResponseEntity.ok(
                    collectivityService.createMembershipFees(id, fees).stream()
                            .map(membershipFeeDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/financialAccounts")
    public ResponseEntity<?> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam(required = false) LocalDate at) {
        try {
            return ResponseEntity.ok(
                    collectivityService.getFinancialAccounts(id).stream()
                            .map(financialAccountDtoMapper::mapToDto)
                            .toList());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/transactions")
    public ResponseEntity<?> getTransactions(
            @PathVariable String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        try {
            return ResponseEntity.ok(
                    collectivityService.getTransactions(id, from, to).stream()
                            .map(transactionDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}