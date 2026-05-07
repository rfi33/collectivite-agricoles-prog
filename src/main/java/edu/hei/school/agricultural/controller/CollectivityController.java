package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.CollectivityInformation;
import edu.hei.school.agricultural.controller.dto.CreateCollectivity;
import edu.hei.school.agricultural.controller.dto.CreateMembershipFee;
import edu.hei.school.agricultural.controller.mapper.CollectivityDtoMapper;
import edu.hei.school.agricultural.controller.mapper.FinancialAccountDtoMapper;
import edu.hei.school.agricultural.controller.mapper.MembershipFeeDtoMapper;
import edu.hei.school.agricultural.controller.mapper.TransactionDtoMapper;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.TransactionRepository;
import edu.hei.school.agricultural.service.CollectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CollectivityController {

    private final CollectivityDtoMapper collectivityDtoMapper;
    private final MembershipFeeDtoMapper membershipFeeDtoMapper;
    private final CollectivityService collectivityService;
    private final FinancialAccountDtoMapper financialAccountDtoMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionDtoMapper transactionDtoMapper;

    @GetMapping("/collectivities/{id}")
    public ResponseEntity<?> getCollectivityById(@PathVariable String id) {
        try {
            return ResponseEntity
                    .ok(collectivityDtoMapper.mapToDto(collectivityService.getCollectivityById(id)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/collectivities")
    public ResponseEntity<?> createCollectivity(@RequestBody List<CreateCollectivity> createCollectivities) {
        try {
            List<Collectivity> collectivities = createCollectivities.stream()
                    .map(collectivityDtoMapper::mapToEntity)
                    .toList();
            return ResponseEntity
                    .ok(collectivityService.createCollectivities(collectivities).stream()
                            .map(collectivityDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/collectivities/{id}/informations")
    public ResponseEntity<?> updateCollectivityInformation(
            @PathVariable String id,
            @RequestBody CollectivityInformation collectivityInformation) {
        String name = collectivityInformation.getName();
        Integer number = collectivityInformation.getNumber();
        try {
            return ResponseEntity
                    .ok(collectivityDtoMapper.mapToDto(
                            collectivityService.updateInformations(id, name, number)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> getCollectivityMembershipFeesByCollectivity(@PathVariable String id) {
        try {
            return ResponseEntity
                    .ok(collectivityService.getMembershipFeesByCollectivityIdentifier(id).stream()
                            .map(membershipFeeDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> createCollectivityMembershipFee(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFee> membershipFees) {
        try {
            List<MembershipFee> membershipFeesToCreate = membershipFees.stream()
                    .map(membershipFeeDtoMapper::mapToEntity)
                    .toList();
            return ResponseEntity
                    .ok(collectivityService.createMembershipFees(id, membershipFeesToCreate).stream()
                            .map(membershipFeeDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/financialAccounts")
    public ResponseEntity<?> getCollectivityFinancialAccounts(
            @PathVariable String id,
            @RequestParam(required = false) LocalDate at) {
        try {
            return ResponseEntity
                    .ok(collectivityService.getFinancialAccounts(id).stream()
                            .map(financialAccount -> financialAccountDtoMapper.mapToDto(financialAccount, at))
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/collectivities/{id}/transactions")
    public ResponseEntity<?> getCollectivityTransactions(
            @PathVariable String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        try {
            collectivityService.getCollectivityById(id);
            return ResponseEntity
                    .ok(transactionRepository.findByCollectivityIdAndPeriod(id, from, to).stream()
                            .map(transactionDtoMapper::mapToDto)
                            .toList());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}