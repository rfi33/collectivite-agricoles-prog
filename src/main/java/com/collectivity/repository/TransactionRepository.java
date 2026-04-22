package com.collectivity.repository;

import com.collectivity.entity.CollectivityTransaction;
import com.collectivity.entity.PaymentMode;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final Connection connection;

    public TransactionRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(CollectivityTransaction tx) {
        String sql = "INSERT INTO collectivities_transactions (id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setDate(2, Date.valueOf(tx.getCreationDate()));
            ps.setDouble(3, tx.getAmount());
            ps.setString(4, tx.getCollectivityId());
            ps.setString(5, tx.getMemberId());
            ps.setString(6, tx.getAccountCreditedId());
            ps.setString(7, tx.getPaymentMode().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de l'enregistrement de la transaction", e);
        }
    }
    public List<CollectivityTransaction> findByPeriod(String collectivityId, LocalDate from, LocalDate to) {
        List<CollectivityTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM collectivities_transactions WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(new CollectivityTransaction(
                        rs.getString("id"),
                        rs.getDate("creation_date").toLocalDate(),
                        rs.getDouble("amount"),
                        rs.getString("collectivity_id"),
                        rs.getString("member_id"),
                        rs.getString("account_credited_id"),
                        PaymentMode.valueOf(rs.getString("payment_mode"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des transactions", e);
        }
        return transactions;
    }
}