package com.collectivity.repository;

import com.collectivity.entity.FinancialAccount;
import com.collectivity.mapper.FinancialAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FinancialAccountRepository {
    private final Connection connection;
    private final FinancialAccountMapper financialAccountMapper;

    public List<FinancialAccount> findByCollectivityId(String collectivityId) {
        String sql = """
                SELECT id, collectivity_id, account_type, amount, holder_name,
                       mobile_money, mobile_number, bank_name,
                       bank_code, bank_branch_code, bank_account_number, bank_account_key
                FROM financial_accounts WHERE collectivity_id = ?
                """;
        List<FinancialAccount> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(financialAccountMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Optional<FinancialAccount> findById(String id) {
        String sql = """
                SELECT id, collectivity_id, account_type, amount, holder_name,
                       mobile_money, mobile_number, bank_name,
                       bank_code, bank_branch_code, bank_account_number, bank_account_key
                FROM financial_accounts WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(financialAccountMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void updateAmount(String accountId, double newAmount) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE financial_accounts SET amount = ? WHERE id = ?")) {
            ps.setDouble(1, newAmount);
            ps.setString(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}