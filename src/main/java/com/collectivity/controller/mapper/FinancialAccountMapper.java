package com.collectivity.controller.mapper;

import com.collectivity.entity.FinancialAccount;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FinancialAccountMapper {

    public FinancialAccount mapFromResultSet(ResultSet rs) throws SQLException {
        return FinancialAccount.builder()
                .id(rs.getString("id"))
                .collectivityId(rs.getString("collectivity_id"))
                .accountType(rs.getString("account_type"))
                .amount(rs.getBigDecimal("amount"))
                .holderName(rs.getString("holder_name"))
                .mobileMoney(rs.getString("mobile_money"))
                .mobileNumber(rs.getString("mobile_number"))
                .bankName(rs.getString("bank_name"))
                .bankCode(rs.getObject("bank_code") == null ? null : rs.getInt("bank_code"))
                .bankBranchCode(rs.getObject("bank_branch_code") == null ? null : rs.getInt("bank_branch_code"))
                .bankAccountNumber(rs.getObject("bank_account_number") == null ? null : rs.getLong("bank_account_number"))
                .bankAccountKey(rs.getObject("bank_account_key") == null ? null : rs.getInt("bank_account_key"))
                .build();
    }
}