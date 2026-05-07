package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.*;
import edu.hei.school.agricultural.exception.InternalServerErrorException;
import edu.hei.school.agricultural.mapper.FinancialAccountMapper;
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

    public Optional<FinancialAccount> findFinancialAccountById(String id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "select id from cash_account where id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CashAccount cashAccount = financialAccountMapper.mapCashFromResultSet(rs);
                cashAccount.addTransactions(getTransactionsByFinancialAccountId(id));
                return Optional.of(cashAccount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "select id, holder_name, bank_name, bank_code, branch_code, account_number, key from bank_account where id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BankAccount bankAccount = financialAccountMapper.mapBankFromResultSet(rs);
                bankAccount.addTransactions(getTransactionsByFinancialAccountId(id));
                return Optional.of(bankAccount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "select id, holder_name, service, mobile_number from mobile_banking_account where id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MobileBankingAccount mobileBankingAccount = financialAccountMapper.mapMobileBankingFromResultSet(rs);
                mobileBankingAccount.addTransactions(getTransactionsByFinancialAccountId(id));
                return Optional.of(mobileBankingAccount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<BankAccount> getBankAccountsByCollectivityId(String collectivityId) {
        List<BankAccount> bankAccounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, holder_name, bank_name, bank_code, branch_code, account_number, key
                from "bank_account" where collectivity_id = ?
                """)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BankAccount bankAccount = financialAccountMapper.mapBankFromResultSet(rs);
                bankAccount.addTransactions(getTransactionsByFinancialAccountId(bankAccount.getId()));
                bankAccounts.add(bankAccount);
            }
            return bankAccounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MobileBankingAccount> getMobileBankingAccountsByCollectivityId(String collectivityId) {
        List<MobileBankingAccount> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, holder_name, service, mobile_number
                from "mobile_banking_account" where collectivity_id = ?
                """)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MobileBankingAccount account = financialAccountMapper.mapMobileBankingFromResultSet(rs);
                account.addTransactions(getTransactionsByFinancialAccountId(account.getId()));
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CashAccount getCashAccountByCollectivityId(String collectivityId) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id from "cash_account" where collectivity_id = ?
                """)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CashAccount cashAccount = financialAccountMapper.mapCashFromResultSet(rs);
                cashAccount.addTransactions(getTransactionsByFinancialAccountId(cashAccount.getId()));
                return cashAccount;
            }
            throw new InternalServerErrorException("Unable to retrieve cash account for collectivity.id= " + collectivityId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getTransactionsByFinancialAccountId(String financialAccountId) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, amount, creation_date, transaction_type
                from "transaction" where financial_account_id = ?
                """)) {
            ps.setString(1, financialAccountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(financialAccountMapper.mapTransactionFromResultSet(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}