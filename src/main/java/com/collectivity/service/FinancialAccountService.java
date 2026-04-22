package com.collectivity.service;

import com.collectivity.dto.request.CreateFinancialAccountRequest;
import com.collectivity.dto.response.FinancialAccountResponse;
import com.collectivity.entity.AccountType;
import com.collectivity.entity.FinancialAccount;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.FinancialAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final CollectivityRepository     collectivityRepository;

    public FinancialAccountService(FinancialAccountRepository financialAccountRepository,
                                   CollectivityRepository collectivityRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.collectivityRepository     = collectivityRepository;
    }

    public List<FinancialAccountResponse> findByCollectivityId(String collectivityId) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new NotFoundException("Collectivity not found: " + collectivityId);
        }
        return financialAccountRepository.findByCollectivityId(collectivityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FinancialAccountResponse create(String collectivityId,
                                           CreateFinancialAccountRequest request) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new NotFoundException("Collectivity not found: " + collectivityId);
        }
        if (request.accountType == null) {
            throw new BadRequestException("Account type is required.");
        }

        // Une seule caisse par collectivité
        if (request.accountType == AccountType.CASH
                && financialAccountRepository.hasCashAccount(collectivityId)) {
            throw new BadRequestException(
                    "Collectivity already has a cash account. Only one cash account is allowed.");
        }

        // Validation selon le type
        switch (request.accountType) {
            case BANK -> {
                if (request.holderName == null || request.bankName == null
                        || request.bankCode == null || request.bankBranchCode == null
                        || request.bankAccountNumber == null || request.bankAccountKey == null) {
                    throw new BadRequestException(
                            "Bank account requires: holderName, bankName, bankCode, bankBranchCode, bankAccountNumber, bankAccountKey.");
                }
            }
            case MOBILE_BANKING -> {
                if (request.holderName == null || request.mobileMoney == null
                        || request.mobileNumber == null) {
                    throw new BadRequestException(
                            "Mobile banking account requires: holderName, mobileBankingService, mobileNumber.");
                }
            }
            case CASH -> {
                // Aucun champ supplémentaire requis
            }
        }

        FinancialAccount account = new FinancialAccount();
        account.collectivityId       = collectivityId;
        account.accountType          = request.accountType;
        account.amount               = BigDecimal.ZERO;
        account.holderName           = request.holderName;
        account.bankName             = request.bankName;
        account.bankCode             = request.bankCode;
        account.bankBranchCode       = request.bankBranchCode;
        account.bankAccountNumber    = request.bankAccountNumber;
        account.bankAccountKey       = request.bankAccountKey;
        account.mobileMoney = request.mobileMoney;
        account.mobileNumber         = request.mobileNumber;

        return toResponse(financialAccountRepository.save(account));
    }

    public FinancialAccountResponse findResponseById(String id) {
        FinancialAccount account = financialAccountRepository.findById(id);
        if (account == null) return null;
        return toResponse(account);
    }

    public FinancialAccount findById(String id) {
        return financialAccountRepository.findById(id);
    }

    public FinancialAccountResponse toResponse(FinancialAccount account) {
        FinancialAccountResponse res = new FinancialAccountResponse();
        res.id                   = account.id;
        res.accountType          = account.accountType;
        res.amount               = account.amount;
        res.holderName           = account.holderName;
        res.bankName             = account.bankName;
        res.bankCode             = account.bankCode;
        res.bankBranchCode       = account.bankBranchCode;
        res.bankAccountNumber    = account.bankAccountNumber;
        res.bankAccountKey       = account.bankAccountKey;
        res.mobileMoney = account.mobileMoney;
        res.mobileNumber         = account.mobileNumber;
        return res;
    }
}