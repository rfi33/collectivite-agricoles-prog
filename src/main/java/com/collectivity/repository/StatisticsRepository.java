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
    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

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

        String dueSql = """
                SELECT amount, frequency, eligible_from
                FROM membership_fee
                WHERE collectivity_id = ?
                  AND status = 'ACTIVE'
                  AND eligible_from <= ?
                """;

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

    public List<CollectivityOverallStatistics> getOverallStatistics(
            LocalDate from, LocalDate to) {




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


        String paidSql = """
                SELECT
                    collectivity_id,
                    member_id,
                    SUM(amount) AS paid_amount
                FROM collectivities_transactions
                WHERE creation_date BETWEEN ? AND ?
                GROUP BY collectivity_id, member_id
                """;

        String activeFeesSql = """
                SELECT collectivity_id, amount, frequency, eligible_from
                FROM membership_fee
                WHERE status = 'ACTIVE'
                  AND eligible_from <= ?
                """;

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

                java.util.Map<String, Double> memberPayments =
                        paidByCollAndMember.getOrDefault(collId, java.util.Collections.emptyMap());

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

    private double computeOccurrencesOnPeriod(double feeAmount, String frequency,
                                               LocalDate eligibleFrom,
                                               LocalDate from, LocalDate to) {

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