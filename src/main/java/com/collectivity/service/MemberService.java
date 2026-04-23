package com.collectivity.service;

import com.collectivity.dto.request.CreateMemberPaymentRequest;
import com.collectivity.dto.request.CreateMemberRequest;
import com.collectivity.dto.request.RefereeInfoRequest;
import com.collectivity.dto.response.FinancialAccountResponse;
import com.collectivity.dto.response.MemberPaymentResponse;
import com.collectivity.dto.response.MemberResponse;
import com.collectivity.entity.CollectivityTransaction;
import com.collectivity.entity.FinancialAccount;
import com.collectivity.entity.Member;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.FinancialAccountRepository;
import com.collectivity.repository.MemberRepository;
import com.collectivity.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository           memberRepository;
    private final TransactionRepository      transactionRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public MemberService(MemberRepository memberRepository,
                         TransactionRepository transactionRepository,
                         FinancialAccountRepository financialAccountRepository) {
        this.memberRepository           = memberRepository;
        this.transactionRepository      = transactionRepository;
        this.financialAccountRepository = financialAccountRepository;
    }

    public List<MemberResponse> createAll(List<CreateMemberRequest> requests) {
        List<MemberResponse> responses = new ArrayList<>();
        for (CreateMemberRequest request : requests) {
            responses.add(create(request));
        }
        return responses;
    }

    private MemberResponse create(CreateMemberRequest request) {
        if (request.collectivityIdentifier == null) {
            throw new BadRequestException("Collectivity identifier is required.");
        }
        if (Boolean.FALSE.equals(request.registrationFeePaid)) {
            throw new BadRequestException("Registration fee must be paid.");
        }
        if (Boolean.FALSE.equals(request.membershipDuesPaid)) {
            throw new BadRequestException("Membership dues must be paid.");
        }
        if (request.referees == null || request.referees.size() < 2) {
            throw new BadRequestException("At least 2 confirmed referees are required.");
        }

        int internalCount = 0;
        int externalCount = 0;

        for (RefereeInfoRequest refereeInfo : request.referees) {
            Member referee = memberRepository.findById(refereeInfo.memberId);
            if (referee == null) {
                throw new NotFoundException("Referee not found: " + refereeInfo.memberId);
            }
            if (!memberRepository.isConfirmedWithSeniority(refereeInfo.memberId)) {
                throw new BadRequestException(
                        "Referee " + refereeInfo.memberId
                                + " is not a confirmed member with more than 90 days seniority.");
            }
            String refereeCollectivityId = memberRepository.getCollectivityIdOf(refereeInfo.memberId);
            if (request.collectivityIdentifier.equals(refereeCollectivityId)) {
                internalCount++;
            } else {
                externalCount++;
            }
        }

        if (internalCount < externalCount) {
            throw new BadRequestException(
                    "The number of referees from the target collectivity must be >= the number from other collectivities.");
        }

        Member member = new Member();
        member.firstName      = request.firstName;
        member.lastName       = request.lastName;
        member.birthDate      = request.birthDate;
        member.gender         = request.gender;
        member.address        = request.address;
        member.profession     = request.profession;
        member.phoneNumber    = request.phoneNumber;
        member.email          = request.email;
        member.occupation     = request.occupation;
        member.collectivityId = request.collectivityIdentifier;

        Member saved = memberRepository.save(member);

        List<String> refereeIds = new ArrayList<>();
        List<String> relations  = new ArrayList<>();
        for (RefereeInfoRequest refereeInfo : request.referees) {
            refereeIds.add(refereeInfo.memberId);
            relations.add(refereeInfo.relation);
        }
        memberRepository.saveReferees(saved.id, refereeIds, relations);
        saved.referees = memberRepository.findRefereesByMemberId(saved.id);

        return toResponse(saved);
    }

    public List<MemberPaymentResponse> createPayments(String memberId,
                                                      List<CreateMemberPaymentRequest> requests) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        List<MemberPaymentResponse> responses = new ArrayList<>();
        for (CreateMemberPaymentRequest request : requests) {
            responses.add(createPayment(member, request));
        }
        return responses;
    }

    private MemberPaymentResponse createPayment(Member member,
                                                CreateMemberPaymentRequest request) {
        FinancialAccount account =
                financialAccountRepository.findById(request.getAccountCreditedIdentifier());
        if (account == null) {
            throw new NotFoundException(
                    "Financial account not found: " + request.getAccountCreditedIdentifier());
        }

        CollectivityTransaction transaction = new CollectivityTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setCreationDate(LocalDate.now());
        transaction.setMemberId(member.id);
        transaction.setCollectivityId(member.collectivityId);
        transaction.setAccountCreditedId(account.id);
        transaction.setPaymentMode(request.getPaymentMode());

        // save() génère et assigne l'id dans la transaction
        CollectivityTransaction saved = transactionRepository.save(transaction);

        // Mise à jour du solde du compte crédité
        financialAccountRepository.credit(account.id, BigDecimal.valueOf(request.getAmount()));

        // Rechargement du compte pour avoir le solde à jour dans la réponse
        FinancialAccount updatedAccount = financialAccountRepository.findById(account.id);

        MemberPaymentResponse response = new MemberPaymentResponse();
        response.id              = saved.getId();
        response.amount          = request.getAmount();
        response.paymentMode     = request.getPaymentMode();
        response.accountCredited = toFinancialAccountResponse(updatedAccount);
        response.creationDate    = saved.getCreationDate();
        return response;
    }

    public MemberResponse toResponse(Member m) {
        MemberResponse response = new MemberResponse();
        response.id          = m.id;
        response.firstName   = m.firstName;
        response.lastName    = m.lastName;
        response.birthDate   = m.birthDate;
        response.gender      = m.gender;
        response.address     = m.address;
        response.profession  = m.profession;
        response.phoneNumber = m.phoneNumber;
        response.email       = m.email;
        response.occupation  = m.occupation;
        if (m.referees != null) {
            response.referees = m.referees.stream().map(this::toResponse).toList();
        }
        return response;
    }

    private FinancialAccountResponse toFinancialAccountResponse(FinancialAccount account) {
        FinancialAccountResponse res = new FinancialAccountResponse();
        res.id                = account.id;
        res.accountType       = account.accountType;
        res.amount            = account.amount;
        res.holderName        = account.holderName;
        res.bankName          = account.bankName;
        res.bankCode          = account.bankCode;
        res.bankBranchCode    = account.bankBranchCode;
        res.bankAccountNumber = account.bankAccountNumber;
        res.bankAccountKey    = account.bankAccountKey;
        res.mobileMoney       = account.mobileMoney;
        res.mobileNumber      = account.mobileNumber;
        return res;
    }
}