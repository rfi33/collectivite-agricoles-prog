package com.collectivity.service;

import com.collectivity.entity.*;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.collectivity.entity.ActivityStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    public Collectivity getCollectivityById(String id) {
        return collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collectivity.id=" + id + " not found"));
    }

    public List<Collectivity> createCollectivities(List<Collectivity> collectivities) {
        for (Collectivity c : collectivities) {
            if (!c.hasEnoughMembers()) {
                throw new BadRequestException(
                        "Collectivity must have at least 10 members, got "
                                + (c.getMembers() == null ? 0 : c.getMembers().size()));
            }
            if (!Boolean.TRUE.equals(c.getFederationApproval())) {
                throw new BadRequestException("Collectivity must have federation approval");
            }
            if (c.getId() == null) c.setId(UUID.randomUUID().toString());
        }
        return collectivityRepository.saveAll(collectivities);
    }

    public Collectivity updateInformations(String id, String name, Integer number) {
        Collectivity c = collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collectivity.id=" + id + " not found"));
        if (name != null && collectivityRepository.isNameExists(name)) {
            throw new BadRequestException("Collectivity.name=" + name + " already exists");
        }
        if (number != null && collectivityRepository.isNumberExists(number)) {
            throw new BadRequestException("Collectivity.number=" + number + " already exists");
        }
        c.setName(name);
        c.setNumber(number);
        return collectivityRepository.saveAll(List.of(c)).getFirst();
    }

    public List<MembershipFee> getMembershipFeesByCollectivityId(String collectivityId) {
        getCollectivityById(collectivityId);
        return membershipFeeRepository.findByCollectivityId(collectivityId);
    }

    public List<MembershipFee> createMembershipFees(String collectivityId, List<MembershipFee> fees) {
        getCollectivityById(collectivityId);
        for (MembershipFee f : fees) {
            f.setId(UUID.randomUUID().toString());
            f.setStatus(ACTIVE);
            f.setCollectivityId(collectivityId);
        }
        return membershipFeeRepository.saveAll(fees);
    }

    public List<FinancialAccount> getFinancialAccounts(String collectivityId) {
        getCollectivityById(collectivityId);
        return financialAccountRepository.findByCollectivityId(collectivityId);
    }

    public List<CollectivityTransaction> getTransactions(
            String collectivityId, LocalDate from, LocalDate to) {
        getCollectivityById(collectivityId);
        return transactionRepository.findByCollectivityAndPeriod(collectivityId, from, to);
    }

    public List<MemberPayment> createMemberPayments(
            String memberId, List<CreateMemberPaymentRequest> requests) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member.id=" + memberId + " not found"));

        return requests.stream().map(req -> {
            FinancialAccount account = financialAccountRepository
                    .findById(req.getAccountCreditedIdentifier())
                    .orElseThrow(() -> new NotFoundException(
                            "FinancialAccount.id=" + req.getAccountCreditedIdentifier() + " not found"));

            BigDecimal amount = BigDecimal.valueOf(req.getAmount());
            LocalDate now = LocalDate.now();

            CollectivityTransaction tx = CollectivityTransaction.builder()
                    .id(UUID.randomUUID().toString())
                    .creationDate(now)
                    .amount(amount)
                    .paymentMode(req.getPaymentMode())
                    .accountCredited(account)
                    .memberDebited(member)
                    .build();
            transactionRepository.save(tx);

            double newAmount = (account.getAmount() == null ? BigDecimal.ZERO : account.getAmount())
                    .add(amount).doubleValue();
            financialAccountRepository.updateAmount(account.getId(), newAmount);

            return MemberPayment.builder()
                    .id(tx.getId())
                    .amount(amount)
                    .paymentMode(req.getPaymentMode())
                    .accountCredited(account)
                    .creationDate(now)
                    .build();
        }).toList();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMemberPaymentRequest {
        private Integer amount;
        private String membershipFeeIdentifier;
        private String accountCreditedIdentifier;
        private PaymentMode paymentMode;
    }
}