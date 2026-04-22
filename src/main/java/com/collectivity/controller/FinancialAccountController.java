package com.collectivity.controller;

import com.collectivity.dto.request.CreateFinancialAccountRequest;
import com.collectivity.dto.response.FinancialAccountResponse;
import com.collectivity.service.FinancialAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class FinancialAccountController {

    private final FinancialAccountService financialAccountService;

    public FinancialAccountController(FinancialAccountService financialAccountService) {
        this.financialAccountService = financialAccountService;
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<FinancialAccountResponse>> getAccounts(
            @PathVariable String id) {
        return ResponseEntity.ok(financialAccountService.findByCollectivityId(id));
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