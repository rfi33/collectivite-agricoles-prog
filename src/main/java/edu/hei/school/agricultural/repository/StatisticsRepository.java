package edu.hei.school.agricultural.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final Connection connection;

    /**
     * Pour chaque membre actif d'une collectivité, retourne :
     * - le montant total encaissé (somme des paiements) sur la période [from, to]
     * - le montant impayé potentiel (cotisations ACTIVE dues mais non réglées) sur la période
     *
     * Un membre est "actif" s'il est inscrit dans la collectivité (présent dans collectivity_member).
     */
    public List<Map<String, Object>> getMemberStatsByCollectivityAndPeriod(
            String collectivityId, LocalDate from, LocalDate to) {

        List<Map<String, Object>> results = new ArrayList<>();

        // On récupère tous les membres actifs de la collectivité
        // avec leur montant encaissé sur la période
        String sql = """
                SELECT
                    m.id                AS member_id,
                    m.first_name        AS first_name,
                    m.last_name         AS last_name,
                    m.email             AS email,
                    m.occupation        AS occupation,
                    COALESCE(SUM(mp.amount), 0) AS earned_amount
                FROM member m
                JOIN collectivity_member cm ON cm.member_id = m.id
                LEFT JOIN member_payment mp
                       ON mp.member_debited_id = m.id
                      AND mp.creation_date BETWEEN ? AND ?
                      AND mp.financial_account_id IN (
                            SELECT id FROM cash_account            WHERE collectivity_id = ?
                            UNION ALL
                            SELECT id FROM bank_account            WHERE collectivity_id = ?
                            UNION ALL
                            SELECT id FROM mobile_banking_account  WHERE collectivity_id = ?
                      )
                WHERE cm.collectivity_id = ?
                GROUP BY m.id, m.first_name, m.last_name, m.email, m.occupation
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
            throw new RuntimeException(e);
        }

        // Pour chaque membre, on calcule le montant impayé potentiel
        // en se basant sur les cotisations ACTIVE de la collectivité éligibles sur la période
        for (Map<String, Object> row : results) {
            double unpaid = computeUnpaidAmount(
                    collectivityId,
                    (String) row.get("member_id"),
                    from, to,
                    (Double) row.get("earned_amount"));
            row.put("unpaid_amount", unpaid);
        }

        return results;
    }

    /**
     * Calcule le montant théoriquement dû par le membre sur la période
     * (d'après les cotisations ACTIVE de la collectivité), puis soustrait
     * ce qu'il a déjà payé.
     * Si le résultat est négatif (paiement excédentaire), on retourne 0.
     */
    private double computeUnpaidAmount(
            String collectivityId,
            String memberId,
            LocalDate from, LocalDate to,
            double earnedAmount) {

        // Somme des montants dus par les cotisations ACTIVE éligibles sur la période
        String sql = """
                SELECT COALESCE(SUM(mf.amount), 0) AS total_due
                FROM membership_fee mf
                WHERE mf.collectivity_id = ?
                  AND mf.status = 'ACTIVE'
                  AND mf.eligible_from <= ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            // Une cotisation est due si elle est éligible avant ou pendant la période
            ps.setDate(2, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalDue = rs.getDouble("total_due");
                double unpaid = totalDue - earnedAmount;
                return Math.max(unpaid, 0.0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    /**
     * Pour chaque collectivité, retourne :
     * - le nombre de nouveaux adhérents sur la période [from, to]
     * - le % de membres à jour dans leurs cotisations (cotisations ACTIVE)
     *
     * Un membre est "à jour" si le total de ses paiements vers la collectivité
     * >= somme des cotisations ACTIVE éligibles de la collectivité sur la période.
     */
    public List<Map<String, Object>> getOverallStatisticsForAllCollectivities(
            LocalDate from, LocalDate to) {

        List<Map<String, Object>> results = new ArrayList<>();

        // 1. Récupérer toutes les collectivités
        String collectivitiesSql = """
                SELECT id, name, number FROM collectivity
                """;

        try (PreparedStatement ps = connection.prepareStatement(collectivitiesSql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("collectivity_id",   rs.getString("id"));
                row.put("collectivity_name", rs.getString("name"));
                row.put("collectivity_number", rs.getObject("number"));
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 2. Pour chaque collectivité, calculer les stats
        for (Map<String, Object> row : results) {
            String colId = (String) row.get("collectivity_id");

            // Nombre de nouveaux adhérents sur la période
            int newMembers = countNewMembers(colId, from, to);
            row.put("new_members_number", newMembers);

            // % de membres à jour dans leurs cotisations
            double percentage = computeCurrentDuePercentage(colId, from, to);
            row.put("overall_member_current_due_percentage", percentage);
        }

        return results;
    }

    /**
     * Compte les membres dont la date d'adhésion (join_date dans collectivity_member)
     * tombe dans la période [from, to].
     * Si join_date n'existe pas dans collectivity_member, on utilise la colonne join_date du membre.
     */
    private int countNewMembers(String collectivityId, LocalDate from, LocalDate to) {
        // On essaie d'abord avec join_date dans collectivity_member
        // Sinon on utilise un fallback sur la table member
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM collectivity_member cm
                JOIN member m ON m.id = cm.member_id
                WHERE cm.collectivity_id = ?
                  AND COALESCE(cm.join_date, m.join_date) BETWEEN ? AND ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            // fallback : si join_date n'existe pas dans les deux tables
            try {
                return countNewMembersFallback(collectivityId, from, to);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return 0;
    }

    private int countNewMembersFallback(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM collectivity_member cm
                WHERE cm.collectivity_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Calcule le pourcentage de membres à jour dans leurs cotisations.
     * Un membre est "à jour" si la somme de ses paiements vers la collectivité
     * >= somme des montants des cotisations ACTIVE éligibles sur la période.
     * Les cotisations INACTIVE ne sont pas prises en compte (même si impayées).
     */
    private double computeCurrentDuePercentage(String collectivityId, LocalDate from, LocalDate to) {
        // Montant total dû par cotisations ACTIVE éligibles sur la période
        double totalDue = getActiveMembershipFeesDue(collectivityId, to);

        if (totalDue <= 0) {
            // Pas de cotisation active => tous les membres sont "à jour" par défaut
            return 100.0;
        }

        // Nombre total de membres de la collectivité
        int totalMembers = countTotalMembers(collectivityId);
        if (totalMembers == 0) return 0.0;

        // Nombre de membres dont le total payé >= totalDue sur la période
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM (
                    SELECT cm.member_id,
                           COALESCE(SUM(mp.amount), 0) AS paid
                    FROM collectivity_member cm
                    LEFT JOIN member_payment mp
                           ON mp.member_debited_id = cm.member_id
                          AND mp.creation_date BETWEEN ? AND ?
                          AND mp.financial_account_id IN (
                                SELECT id FROM cash_account           WHERE collectivity_id = ?
                                UNION ALL
                                SELECT id FROM bank_account           WHERE collectivity_id = ?
                                UNION ALL
                                SELECT id FROM mobile_banking_account WHERE collectivity_id = ?
                          )
                    WHERE cm.collectivity_id = ?
                    GROUP BY cm.member_id
                    HAVING COALESCE(SUM(mp.amount), 0) >= ?
                ) AS up_to_date_members
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setString(3, collectivityId);
            ps.setString(4, collectivityId);
            ps.setString(5, collectivityId);
            ps.setString(6, collectivityId);
            ps.setDouble(7, totalDue);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int upToDate = rs.getInt("cnt");
                return (upToDate * 100.0) / totalMembers;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    private double getActiveMembershipFeesDue(String collectivityId, LocalDate to) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0) AS total
                FROM membership_fee
                WHERE collectivity_id = ?
                  AND status = 'ACTIVE'
                  AND eligible_from <= ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    private int countTotalMembers(String collectivityId) {
        String sql = "SELECT COUNT(*) AS cnt FROM collectivity_member WHERE collectivity_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
