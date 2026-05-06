package com.collectivity.repository;

import com.collectivity.controller.dto.CollectivityInformation;
import com.collectivity.controller.dto.CollectivityLocalStatistics;
import com.collectivity.controller.dto.CollectivityOverallStatistics;
import com.collectivity.controller.dto.MemberDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final Connection connection;

    /**
     * Endpoint G — GET /collectivites/{id}/statistics
     *
     * Push-down processing : tout le calcul est réalisé directement en SQL.
     *
     * Pour chaque membre actif de la collectivité sur la période [from, to] :
     *
     * - earnedAmount : somme réelle des transactions du membre vers cette collectivité
     *   sur la période (depuis collectivities_transactions).
     *
     * - unpaidAmount : pour chaque cotisation ACTIVE de la collectivité dont eligible_from
     *   est <= to, on calcule le montant théoriquement dû sur la période selon la fréquence,
     *   puis on soustrait ce que le membre a réellement payé.
     *   Si le résultat est négatif (membre en avance), on renvoie 0.
     *
     * Seules les cotisations avec status = 'ACTIVE' sont prises en compte.
     * Une cotisation INACTIVE est ignorée des deux calculs.
     */
    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        /*
         * Calcul push-down en deux étapes :
         *
         * Étape 1 — earnedAmount par membre :
         *   On agrège directement via SUM dans collectivities_transactions
         *   en filtrant sur collectivity_id et la période.
         *
         * Étape 2 — montant total dû sur la période (cotisations ACTIVES) :
         *   On récupère toutes les cotisations ACTIVES de la collectivité
         *   dont eligible_from <= to, puis on calcule le nombre d'occurrences
         *   selon la fréquence sur [max(from, eligible_from), to].
         *   Ce montant est identique pour tous les membres (c'est une règle collective).
         *   unpaidAmount = max(0, totalDue - earnedAmount).
         */

        // --- Étape 1 : earnedAmount par membre (push-down SUM) ---
        String earnedSql = """
                SELECT
                    m.id              AS member_id,
                    m.first_name,
                    m.last_name,
                    m.email,
                    m.occupation,
                    COALESCE(SUM(ct.amount), 0) AS earned_amount
                FROM member m
                INNER JOIN collectivity_member cm
                    ON cm.member_id = m.id
                    AND cm.collectivity_id = ?
                LEFT JOIN collectivities_transactions ct
                    ON ct.member_id    = m.id
                    AND ct.collectivity_id = ?
                    AND ct.creation_date BETWEEN ? AND ?
                GROUP BY m.id, m.first_name, m.last_name, m.email, m.occupation
                ORDER BY m.last_name, m.first_name
                """;

        // --- Étape 2 : montant total dû (cotisations ACTIVES, push-down filtre status) ---
        String dueSql = """
                SELECT amount, frequency, eligible_from
                FROM membership_fee
                WHERE collectivity_id = ?
                  AND status = 'ACTIVE'
                  AND eligible_from <= ?
                """;

        // Calcul du montant dû total pour la collectivité (même valeur pour chaque membre)
        double totalDue = 0.0;
        try (PreparedStatement duePs = connection.prepareStatement(dueSql)) {
            duePs.setString(1, collectivityId);
            duePs.setDate(2, java.sql.Date.valueOf(to));
            ResultSet dueRs = duePs.executeQuery();
            while (dueRs.next()) {
                double feeAmount      = dueRs.getDouble("amount");
                String frequency      = dueRs.getString("frequency");
                LocalDate eligibleFrom = dueRs.getDate("eligible_from").toLocalDate();
                totalDue += computeOccurrencesOnPeriod(feeAmount, frequency, eligibleFrom, from, to);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul cotisations dues", e);
        }

        // Construction de la liste des statistiques par membre
        List<CollectivityLocalStatistics> result = new ArrayList<>();
        try (PreparedStatement earnedPs = connection.prepareStatement(earnedSql)) {
            earnedPs.setString(1, collectivityId);
            earnedPs.setString(2, collectivityId);
            earnedPs.setDate(3, java.sql.Date.valueOf(from));
            earnedPs.setDate(4, java.sql.Date.valueOf(to));

            ResultSet rs = earnedPs.executeQuery();
            while (rs.next()) {
                double earned   = rs.getDouble("earned_amount");
                double unpaid   = Math.max(0.0, totalDue - earned);

                MemberDescription memberDescription = MemberDescription.builder()
                        .id(rs.getString("member_id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .occupation(rs.getString("occupation"))
                        .build();

                result.add(CollectivityLocalStatistics.builder()
                        .memberDescription(memberDescription)
                        .earnedAmount(earned)
                        .unpaidAmount(unpaid)
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul statistiques locales", e);
        }

        return result;
    }

    /**
     * Endpoint H — GET /collectivites/statistics
     *
     * Push-down processing : tout le calcul est réalisé directement en SQL.
     *
     * Pour chaque collectivité sur la période [from, to] :
     *
     * - newMembersNumber : COUNT des membres dont la date d'adhésion (collectivity_member.join_date)
     *   tombe dans la période.
     *
     * - overallMemberCurrentDuePercentage : pourcentage de membres dont le total payé
     *   sur la période >= montant total dû (cotisations ACTIVES uniquement).
     *   Si une cotisation est INACTIVE, elle n'est pas comptée dans le montant dû,
     *   donc elle n'impacte pas négativement le pourcentage.
     */
    public List<CollectivityOverallStatistics> getOverallStatistics(
            LocalDate from, LocalDate to) {

        /*
         * Calcul push-down en trois requêtes :
         *
         * Requête A — nouveaux adhérents : COUNT sur collectivity_member.join_date dans [from, to].
         *
         * Requête B — paiements réels : SUM(amount) de collectivities_transactions
         *   sur la période, groupé par (collectivity_id, member_id).
         *
         * Requête C — cotisations ACTIVES : amount + frequency + eligible_from par collectivité,
         *   pour calculer le montant dû collectivité par collectivité.
         *
         * Le % à jour est calculé en Java à partir des données agrégées déjà remontées.
         */

        // Requête A : nouveaux adhérents par collectivité (push-down COUNT + filter join_date)
        String newMembersSql = """
                SELECT
                    c.id     AS collectivity_id,
                    c.name   AS collectivity_name,
                    c.number AS collectivity_number,
                    COUNT(cm.member_id) AS new_members_count
                FROM "collectivity" c
                LEFT JOIN collectivity_member cm
                    ON cm.collectivity_id = c.id
                    AND cm.join_date BETWEEN ? AND ?
                GROUP BY c.id, c.name, c.number
                ORDER BY c.name
                """;

        // Requête B : montant payé par (collectivité, membre) sur la période (push-down SUM)
        String paidSql = """
                SELECT
                    collectivity_id,
                    member_id,
                    SUM(amount) AS paid_amount
                FROM collectivities_transactions
                WHERE creation_date BETWEEN ? AND ?
                GROUP BY collectivity_id, member_id
                """;

        // Requête C : cotisations ACTIVES par collectivité (push-down filtre status = 'ACTIVE')
        String activeFeesSql = """
                SELECT collectivity_id, amount, frequency, eligible_from
                FROM membership_fee
                WHERE status = 'ACTIVE'
                  AND eligible_from <= ?
                """;

        // --- Chargement des paiements (Map collectivityId -> Map memberId -> paidAmount) ---
        java.util.Map<String, java.util.Map<String, Double>> paidByCollAndMember = new java.util.HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(paidSql)) {
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String collId  = rs.getString("collectivity_id");
                String membId  = rs.getString("member_id");
                double paid    = rs.getDouble("paid_amount");
                paidByCollAndMember
                        .computeIfAbsent(collId, k -> new java.util.HashMap<>())
                        .put(membId, paid);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur chargement paiements", e);
        }

        // --- Calcul du montant dû par collectivité (Map collectivityId -> totalDue) ---
        java.util.Map<String, Double> dueByColl = new java.util.HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(activeFeesSql)) {
            ps.setDate(1, java.sql.Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String collId      = rs.getString("collectivity_id");
                double feeAmount   = rs.getDouble("amount");
                String frequency   = rs.getString("frequency");
                LocalDate eligFrom = rs.getDate("eligible_from").toLocalDate();
                double due = computeOccurrencesOnPeriod(feeAmount, frequency, eligFrom, from, to);
                dueByColl.merge(collId, due, Double::sum);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur chargement cotisations actives", e);
        }

        // --- Construction du résultat (push-down nouveaux membres + agrégation %) ---
        List<CollectivityOverallStatistics> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(newMembersSql)) {
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String collId   = rs.getString("collectivity_id");
                String collName = rs.getString("collectivity_name");
                int collNumber  = rs.getInt("collectivity_number");
                int newCount    = rs.getInt("new_members_count");

                double totalDue = dueByColl.getOrDefault(collId, 0.0);

                // Membres de cette collectivité ayant au moins un enregistrement de paiement
                java.util.Map<String, Double> memberPayments =
                        paidByCollAndMember.getOrDefault(collId, java.util.Collections.emptyMap());

                // Calcul du % membres à jour :
                // un membre est à jour si paid >= totalDue (ou si totalDue == 0 : pas de cotisation active)
                long totalMembersWithPayments = memberPayments.size();
                long upToDateMembers = totalMembersWithPayments == 0 ? 0L :
                        memberPayments.values().stream()
                                .filter(paid -> paid >= totalDue)
                                .count();

                double percentage = totalMembersWithPayments == 0
                        ? (totalDue == 0.0 ? 100.0 : 0.0)
                        : (upToDateMembers * 100.0) / totalMembersWithPayments;

                CollectivityInformation info = new CollectivityInformation();
                info.setName(collName);
                info.setNumber(collNumber == 0 ? null : collNumber);

                result.add(CollectivityOverallStatistics.builder()
                        .collectivityInformation(info)
                        .newMembersNumber(newCount)
                        .overallMemberCurrentDuePercentage(percentage)
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul statistiques globales", e);
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Helper : nombre d'occurrences d'une cotisation sur la période [from, to]
    // selon sa fréquence, multiplié par le montant unitaire.
    // Push-down logic : appelé après récupération des données déjà filtrées depuis la DB.
    // -------------------------------------------------------------------------

    private double computeOccurrencesOnPeriod(double feeAmount, String frequency,
                                               LocalDate eligibleFrom,
                                               LocalDate from, LocalDate to) {
        // La cotisation ne commence à courir qu'à partir de eligible_from
        LocalDate effectiveStart = eligibleFrom.isAfter(from) ? eligibleFrom : from;
        if (effectiveStart.isAfter(to)) {
            return 0.0;
        }
        return switch (frequency) {
            case "PUNCTUALLY" -> feeAmount;
            case "WEEKLY" -> {
                long weeks = java.time.temporal.ChronoUnit.WEEKS.between(effectiveStart, to) + 1;
                yield weeks * feeAmount;
            }
            case "MONTHLY" -> {
                long months = java.time.temporal.ChronoUnit.MONTHS.between(
                        effectiveStart.withDayOfMonth(1),
                        to.withDayOfMonth(1)) + 1;
                yield months * feeAmount;
            }
            case "ANNUALLY" -> {
                long years = java.time.temporal.ChronoUnit.YEARS.between(
                        effectiveStart.withDayOfYear(1),
                        to.withDayOfYear(1)) + 1;
                yield years * feeAmount;
            }
            default -> feeAmount;
        };
    }
}