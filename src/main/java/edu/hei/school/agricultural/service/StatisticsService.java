package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityInformation;
import edu.hei.school.agricultural.controller.dto.CollectivityLocalStatistics;
import edu.hei.school.agricultural.controller.dto.CollectivityOverallStatistics;
import edu.hei.school.agricultural.controller.dto.MemberDescription;
import edu.hei.school.agricultural.service.AttendanceStatisticsService.MemberIdWithOccupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DataSource dataSource;
    private final AttendanceStatisticsService attendanceStatisticsService;

    public List<CollectivityLocalStatistics> getMemberStatsByCollectivityAndPeriod(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = """
                WITH member_payments AS (
                    SELECT
                        m.id        AS member_id,
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
                                SELECT id FROM cash_account           WHERE collectivity_id = ?
                                UNION ALL
                                SELECT id FROM bank_account           WHERE collectivity_id = ?
                                UNION ALL
                                SELECT id FROM mobile_banking_account WHERE collectivity_id = ?
                          )
                    WHERE cm.collectivity_id = ?
                    GROUP BY m.id, m.first_name, m.last_name, m.email, m.occupation
                ),
                active_fees_total AS (
                    SELECT COALESCE(SUM(mf.amount), 0) AS total_due
                    FROM membership_fee mf
                    WHERE mf.collectivity_id = ?
                      AND mf.status = 'ACTIVE'
                      AND mf.eligible_from <= ?
                )
                SELECT
                    mp.*,
                    GREATEST(0.0, (SELECT total_due FROM active_fees_total) - mp.earned_amount) AS unpaid_amount
                FROM member_payments mp
                ORDER BY mp.last_name, mp.first_name
                """;

        List<CollectivityLocalStatistics> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            ps.setDate(i++, Date.valueOf(from));
            ps.setDate(i++, Date.valueOf(to));
            ps.setString(i++, collectivityId);
            ps.setString(i++, collectivityId);
            ps.setString(i++, collectivityId);
            ps.setString(i++, collectivityId);
            ps.setString(i++, collectivityId);
            ps.setDate(i++, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String memberId   = rs.getString("member_id");
                String occupation = rs.getString("occupation");

                MemberDescription memberDescription = MemberDescription.builder()
                        .id(memberId)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .occupation(occupation)
                        .build();


                Double assiduityPercentage = attendanceStatisticsService
                        .getMemberAssiduityPercentage(collectivityId, memberId, occupation);

                results.add(CollectivityLocalStatistics.builder()
                        .memberDescription(memberDescription)
                        .earnedAmount(rs.getDouble("earned_amount"))
                        .unpaidAmount(rs.getDouble("unpaid_amount"))
                        .assiduityPercentage(assiduityPercentage)
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error computing member statistics", e);
        }

        return results;
    }

    public List<CollectivityOverallStatistics> getOverallStatisticsForAllCollectivities(
            LocalDate from, LocalDate to) {

        String sql = """
                WITH collectivity_base AS (
                    SELECT c.id AS collectivity_id, c.name, c.number
                    FROM collectivity c
                ),
                new_members_count AS (
                    SELECT
                        cm.collectivity_id,
                        COUNT(DISTINCT CASE
                            WHEN COALESCE(cm.join_date, m.join_date) BETWEEN ? AND ?
                            THEN m.id
                        END) AS new_members
                    FROM collectivity_member cm
                    JOIN member m ON m.id = cm.member_id
                    GROUP BY cm.collectivity_id
                ),
                total_members_count AS (
                    SELECT collectivity_id, COUNT(*) AS total_members
                    FROM collectivity_member
                    GROUP BY collectivity_id
                ),
                active_fees AS (
                    SELECT mf.collectivity_id, COALESCE(SUM(mf.amount), 0) AS total_due
                    FROM membership_fee mf
                    WHERE mf.status = 'ACTIVE'
                      AND mf.eligible_from <= ?
                    GROUP BY mf.collectivity_id
                ),
                members_up_to_date AS (
                    SELECT
                        cm.collectivity_id,
                        COUNT(DISTINCT CASE
                            WHEN COALESCE(
                                (SELECT SUM(mp2.amount)
                                 FROM member_payment mp2
                                 WHERE mp2.member_debited_id = cm.member_id
                                   AND mp2.creation_date BETWEEN ? AND ?
                                   AND mp2.financial_account_id IN (
                                       SELECT id FROM cash_account           WHERE collectivity_id = cm.collectivity_id
                                       UNION ALL
                                       SELECT id FROM bank_account           WHERE collectivity_id = cm.collectivity_id
                                       UNION ALL
                                       SELECT id FROM mobile_banking_account WHERE collectivity_id = cm.collectivity_id
                                   )
                                ), 0) >= COALESCE(af.total_due, 0)
                            THEN cm.member_id
                        END) AS up_to_date_count
                    FROM collectivity_member cm
                    LEFT JOIN active_fees af ON af.collectivity_id = cm.collectivity_id
                    GROUP BY cm.collectivity_id
                )
                SELECT
                    cb.collectivity_id,
                    cb.name,
                    cb.number,
                    COALESCE(nmc.new_members, 0) AS new_members_number,
                    CASE
                        WHEN COALESCE(tmc.total_members, 0) > 0
                        THEN ROUND((COALESCE(mud.up_to_date_count, 0)::numeric
                                    / tmc.total_members::numeric) * 100, 2)
                        ELSE 100.0
                    END AS overall_member_current_due_percentage
                FROM collectivity_base cb
                LEFT JOIN new_members_count     nmc ON nmc.collectivity_id = cb.collectivity_id
                LEFT JOIN total_members_count   tmc ON tmc.collectivity_id = cb.collectivity_id
                LEFT JOIN members_up_to_date    mud ON mud.collectivity_id = cb.collectivity_id
                ORDER BY cb.name
                """;

        String membersSql = """
                SELECT m.id, m.occupation
                FROM member m
                JOIN collectivity_member cm ON cm.member_id = m.id
                WHERE cm.collectivity_id = ?
                """;

        List<CollectivityOverallStatistics> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setDate(3, Date.valueOf(to));
            ps.setDate(4, Date.valueOf(from));
            ps.setDate(5, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String collectivityId = rs.getString("collectivity_id");

                CollectivityInformation collectivityInformation = CollectivityInformation.builder()
                        .name(rs.getString("name"))
                        .number(rs.getInt("number") == 0 && rs.wasNull() ? null : rs.getInt("number"))
                        .build();

                List<MemberIdWithOccupation> membersOfCollectivity =
                        getMembersOfCollectivity(conn, membersSql, collectivityId);

                Double overallAssiduity = attendanceStatisticsService
                        .getCollectivityOverallAssiduityPercentage(collectivityId, membersOfCollectivity);

                results.add(CollectivityOverallStatistics.builder()
                        .collectivityInformation(collectivityInformation)
                        .newMembersNumber(rs.getInt("new_members_number"))
                        .overallMemberCurrentDuePercentage(
                                rs.getDouble("overall_member_current_due_percentage"))
                        .overallMemberAssiduityPercentage(overallAssiduity)
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error computing overall statistics", e);
        }

        return results;
    }

    private List<MemberIdWithOccupation> getMembersOfCollectivity(
            Connection conn, String sql, String collectivityId) throws SQLException {

        List<MemberIdWithOccupation> members = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(new MemberIdWithOccupation(
                        rs.getString("id"),
                        rs.getString("occupation")));
            }
        }
        return members;
    }
}