package com.collectivity.repository;

import com.collectivity.entity.CollectivityTransaction;
import org.springframework.stereotype.Repository;
import java.sql.*;
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
}