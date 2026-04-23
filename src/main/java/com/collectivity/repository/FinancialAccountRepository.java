package com.collectivity.repository;

import com.collectivity.entity.AccountType;
import com.collectivity.entity.Bank;
import com.collectivity.entity.FinancialAccount;
import com.collectivity.entity.MobileMoney;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FinancialAccountRepository {

    private final Connection connection;

    public FinancialAccountRepository(Connection connection) {
        this.connection = connection;
    }

    public FinancialAccount save(FinancialAccount account) {
        String sql = """
            INSERT INTO financial_accounts (
                id, collectivity_id, account_type, amount,
                holder_name, bank_name, bank_code, bank_branch_code,
                bank_account_number, bank_account_key,
                mobile_money, mobile_number
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String id = UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, account.collectivityId);
            ps.setObject(3, toPGEnum("account_type", account.accountType.name()));
            ps.setBigDecimal(4, account.amount != null ? account.amount : java.math.BigDecimal.ZERO);
            ps.setString(5, account.holderName);
            ps.setObject(6, account.bankName != null
                    ? toPGEnum("bank_name", account.bankName.name()) : null);
            setNullableInt(ps, 7, account.bankCode);
            setNullableInt(ps, 8, account.bankBranchCode);
            setNullableLong(ps, 9, account.bankAccountNumber);
            setNullableInt(ps, 10, account.bankAccountKey);
            ps.setObject(11, account.mobileMoney != null
                    ? toPGEnum("mobile_money", account.mobileMoney.name()) : null);
            setNullableLong(ps, 12, account.mobileNumber);
            ps.executeUpdate();
            account.id = id;
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save financial account: " + e.getMessage(), e);
        }
    }

    public FinancialAccount findById(String id) {
        String sql = "SELECT * FROM financial_accounts WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find financial account id=" + id, e);
        }
    }

    public List<FinancialAccount> findByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM financial_accounts WHERE collectivity_id = ?";
        List<FinancialAccount> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find accounts for collectivity id=" + collectivityId, e);
        }
    }

    public boolean hasCashAccount(String collectivityId) {
        String sql = "SELECT 1 FROM financial_accounts WHERE collectivity_id = ? AND account_type = 'CASH'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check cash account for collectivity id=" + collectivityId, e);
        }
    }

    private FinancialAccount mapRow(ResultSet rs) throws SQLException {
        FinancialAccount a = new FinancialAccount();
        a.id             = rs.getString("id");
        a.collectivityId = rs.getString("collectivity_id");
        a.accountType    = AccountType.valueOf(rs.getString("account_type"));
        a.amount         = rs.getBigDecimal("amount");
        a.holderName     = rs.getString("holder_name");

        String bank = rs.getString("bank_name");
        a.bankName = bank != null ? Bank.valueOf(bank) : null;

        int bankCode = rs.getInt("bank_code");
        a.bankCode = rs.wasNull() ? null : bankCode;

        int branchCode = rs.getInt("bank_branch_code");
        a.bankBranchCode = rs.wasNull() ? null : branchCode;

        long accountNumber = rs.getLong("bank_account_number");
        a.bankAccountNumber = rs.wasNull() ? null : accountNumber;

        int accountKey = rs.getInt("bank_account_key");
        a.bankAccountKey = rs.wasNull() ? null : accountKey;
        String mbs = rs.getString("mobile_money");
        a.mobileMoney = mbs != null ? MobileMoney.valueOf(mbs) : null;

        long mobileNumber = rs.getLong("mobile_number");
        a.mobileNumber = rs.wasNull() ? null : mobileNumber;

        return a;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) ps.setInt(index, value);
        else ps.setNull(index, Types.INTEGER);
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value != null) ps.setLong(index, value);
        else ps.setNull(index, Types.BIGINT);
    }

    private PGobject toPGEnum(String typeName, String value) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(typeName);
        pgObject.setValue(value);
        return pgObject;
    }
}