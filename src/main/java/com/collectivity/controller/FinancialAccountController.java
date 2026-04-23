package com.collectivity.controller;

import com.collectivity.dto.request.CreateFinancialAccountRequest;
import com.collectivity.dto.response.FinancialAccountResponse;
import com.collectivity.service.FinancialAccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class FinancialAccountController {

    private final FinancialAccountService financialAccountService;

    public FinancialAccountController(FinancialAccountService financialAccountService) {
        this.financialAccountService = financialAccountService;
    }


    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<List<FinancialAccountResponse>> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate at) {

        List<FinancialAccountResponse> response = (at != null)
                ? financialAccountService.findByCollectivityIdAt(id, at)
                : financialAccountService.findByCollectivityId(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/accounts")
    public ResponseEntity<FinancialAccountResponse> createAccount(
            @PathVariable String id,
            @RequestBody CreateFinancialAccountRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(financialAccountService.create(id, request));
    }
}