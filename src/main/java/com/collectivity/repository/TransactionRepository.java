package com.collectivity.repository;

import com.collectivity.entity.CollectivityTransaction;
import com.collectivity.entity.FinancialAccount;
import com.collectivity.entity.PaymentMode;
import com.collectivity.mapper.FinancialAccountMapper;
import com.collectivity.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
    private final Connection connection;
    private final MemberMapper memberMapper;
    private final FinancialAccountMapper financialAccountMapper;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberRepository memberRepository;

    public List<CollectivityTransaction> findByCollectivityAndPeriod(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = """
                SELECT ct.id, ct.creation_date, ct.amount, ct.payment_mode,
                       ct.account_credited_id, ct.member_id,
                       fa.id AS fa_id, fa.collectivity_id AS fa_coll_id,
                       fa.account_type, fa.amount AS fa_amount, fa.holder_name,
                       fa.mobile_money, fa.mobile_number, fa.bank_name,
                       fa.bank_code, fa.bank_branch_code, fa.bank_account_number, fa.bank_account_key,
                       m.id AS m_id, m.first_name, m.last_name, m.birth_date, m.gender,
                       m.phone_number, m.email, m.address, m.profession,
                       m.occupation, m.registration_fee_paid, m.membership_dues_paid
                FROM collectivities_transactions ct
                JOIN financial_accounts fa ON fa.id = ct.account_credited_id
                JOIN "member" m ON m.id = ct.member_id
                WHERE ct.collectivity_id = ?
                  AND ct.creation_date BETWEEN ? AND ?
                ORDER BY ct.creation_date
                """;
        List<CollectivityTransaction> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FinancialAccount fa = FinancialAccount.builder()
                        .id(rs.getString("fa_id"))
                        .collectivityId(rs.getString("fa_coll_id"))
                        .accountType(rs.getString("account_type"))
                        .amount(rs.getBigDecimal("fa_amount"))
                        .holderName(rs.getString("holder_name"))
                        .mobileMoney(rs.getString("mobile_money"))
                        .mobileNumber(rs.getString("mobile_number"))
                        .bankName(rs.getString("bank_name"))
                        .bankCode(rs.getObject("bank_code") == null ? null : rs.getInt("bank_code"))
                        .bankBranchCode(rs.getObject("bank_branch_code") == null ? null : rs.getInt("bank_branch_code"))
                        .bankAccountNumber(rs.getObject("bank_account_number") == null ? null : rs.getLong("bank_account_number"))
                        .bankAccountKey(rs.getObject("bank_account_key") == null ? null : rs.getInt("bank_account_key"))
                        .build();
                // remap member columns (aliased as m.*)
                var member = memberRepository.findById(rs.getString("m_id")).orElse(null);
                String pm = rs.getString("payment_mode");
                list.add(CollectivityTransaction.builder()
                        .id(rs.getString("id"))
                        .creationDate(rs.getDate("creation_date").toLocalDate())
                        .amount(rs.getBigDecimal("amount"))
                        .paymentMode(pm == null ? null : PaymentMode.valueOf(pm))
                        .accountCredited(fa)
                        .memberDebited(member)
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public CollectivityTransaction save(CollectivityTransaction tx) {
        String sql = """
                INSERT INTO collectivities_transactions
                    (id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode)
                VALUES (?, ?, ?, ?, ?, ?, ?::payment_mode)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tx.getId());
            ps.setDate(2, Date.valueOf(tx.getCreationDate()));
            ps.setBigDecimal(3, tx.getAmount());
            ps.setString(4, tx.getAccountCredited().getCollectivityId());
            ps.setString(5, tx.getMemberDebited().getId());
            ps.setString(6, tx.getAccountCredited().getId());
            ps.setString(7, tx.getPaymentMode().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tx;
    }
}