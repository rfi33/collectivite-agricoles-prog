package edu.hei.school.agricultural.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {
    
    private final DataSource dataSource;
    
    /**
     * Récupère les paiements des membres par collectivité et période.
     * Push-down : filtrage et agrégation dans la requête SQL.
     */
    public List<Map<String, Object>> getMemberPaymentsByCollectivity(
            String collectivityId, LocalDate from, LocalDate to) {
        
        String sql = """
                SELECT 
                    m.id AS member_id,
                    m.first_name,
                    m.last_name,
                    m.email,
                    m.occupation,
                    COALESCE(SUM(mp.amount), 0) AS earned_amount
                FROM member m
                JOIN collectivity_member cm ON cm.member_id = m.id
                LEFT JOIN member_payment mp
                       ON mp.member_debited_id = m.id
                      AND mp.creation_date BETWEEN ? AND ?
                      AND mp.financial_account_id IN (
                            SELECT id FROM cash_account WHERE collectivity_id = ?
                            UNION ALL
                            SELECT id FROM bank_account WHERE collectivity_id = ?
                            UNION ALL
                            SELECT id FROM mobile_banking_account WHERE collectivity_id = ?
                      )
                WHERE cm.collectivity_id = ?
                GROUP BY m.id, m.first_name, m.last_name, m.email, m.occupation
                ORDER BY m.last_name, m.first_name
                """;
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setString(3, collectivityId);
            ps.setString(4, collectivityId);
            ps.setString(5, collectivityId);
            ps.setString(6, collectivityId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("member_id",    rs.getString("member_id"));
                row.put("first_name",   rs.getString("first_name"));
                row.put("last_name",    rs.getString("last_name"));
                row.put("email",        rs.getString("email"));
                row.put("occupation",   rs.getString("occupation"));
                row.put("earned_amount", rs.getDouble("earned_amount"));
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching member payments", e);
        }
        
        return results;
    }
    
    /**
     * Récupère le total des cotisations actives pour une collectivité.
     * Push-down : filtrage et somme dans la requête SQL.
     */
    public Double getActiveFeesTotal(String collectivityId, LocalDate endDate) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0) AS total_due
                FROM membership_fee
                WHERE collectivity_id = ?
                  AND status = 'ACTIVE'
                  AND eligible_from <= ?
                """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_due");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active fees total", e);
        }
        
        return 0.0;
    }
}