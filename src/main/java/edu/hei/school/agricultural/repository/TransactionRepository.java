package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.*;
import edu.hei.school.agricultural.mapper.FinancialAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
    private final Connection connection;
    private final MemberRepository memberRepository;
    private final FinancialAccountMapper financialAccountMapper;
    private final FinancialAccountRepository financialAccountRepository;

    public List<CollectivityTransaction> saveAll(List<CollectivityTransaction> transactionList) {
        List<CollectivityTransaction> savedTransactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                insert into "transaction" (id, amount, creation_date, transaction_type, financial_account_id, member_debited_id)
                values (?, ?, ?, ?::transaction_type, ?, ?)
                """)) {
            for (CollectivityTransaction transaction : transactionList) {
                preparedStatement.setString(1, transaction.getId());
                preparedStatement.setDouble(2, transaction.getAmount());
                preparedStatement.setDate(3, Date.valueOf(transaction.getCreationDate()));
                preparedStatement.setString(4, transaction.getType().name());
                preparedStatement.setString(5, transaction.getAccountCredited().getId());
                preparedStatement.setString(6, transaction.getMemberDebited().getId());
                preparedStatement.addBatch();
            }
            var executedBatch = preparedStatement.executeBatch();
            for (int i = 0; i < executedBatch.length; i++) {
                savedTransactions.add(findById(transactionList.get(i).getId()).orElseThrow());
            }
            return savedTransactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CollectivityTransaction> findByCollectivityIdAndPeriod(String collectivityId, java.time.LocalDate from, java.time.LocalDate to) {
        List<CollectivityTransaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select t.id, t.amount, t.creation_date, t.transaction_type, t.financial_account_id, t.member_debited_id
                from "transaction" t
                where t.financial_account_id in (
                    select id from cash_account where collectivity_id = ?
                    union all
                    select id from bank_account where collectivity_id = ?
                    union all
                    select id from mobile_banking_account where collectivity_id = ?
                )
                and t.creation_date between ? and ?
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setString(3, collectivityId);
            ps.setDate(4, Date.valueOf(from));
            ps.setDate(5, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapFromResultSet(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<CollectivityTransaction> findById(String id) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, amount, creation_date, transaction_type, financial_account_id, member_debited_id
                from "transaction" where id = ?
                """)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFromResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CollectivityTransaction mapFromResultSet(ResultSet rs) throws SQLException {
        Member memberDebited = memberRepository.findById(rs.getString("member_debited_id")).orElseThrow();
        FinancialAccount accountCredited = financialAccountRepository
                .findFinancialAccountById(rs.getString("financial_account_id")).orElseThrow();
        return CollectivityTransaction.builder()
                .id(rs.getString("id"))
                .amount(rs.getDouble("amount"))
                .creationDate(rs.getDate("creation_date").toLocalDate())
                .type(TransactionType.valueOf(rs.getString("transaction_type")))
                .memberDebited(memberDebited)
                .accountCredited(accountCredited)
                .build();
    }
}