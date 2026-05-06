package com.collectivity.repository;

import com.collectivity.entity.MembershipFee;
import com.collectivity.mapper.MembershipFeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MembershipFeeRepository {
    private final Connection connection;
    private final MembershipFeeMapper membershipFeeMapper;

    public List<MembershipFee> findByCollectivityId(String collectivityId) {
        String sql = """
                SELECT id, collectivity_id, label, amount, frequency, eligible_from, status
                FROM membership_fee WHERE collectivity_id = ?
                """;
        List<MembershipFee> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(membershipFeeMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<MembershipFee> saveAll(List<MembershipFee> fees) {
        String sql = """
                INSERT INTO membership_fee
                    (id, collectivity_id, label, amount, eligible_from, frequency, status)
                VALUES (?, ?, ?, ?, ?, ?::frequency, ?::activity_status)
                ON CONFLICT (id) DO UPDATE SET
                    label         = excluded.label,
                    amount        = excluded.amount,
                    eligible_from = excluded.eligible_from,
                    frequency     = excluded.frequency,
                    status        = excluded.status
                """;
        List<MembershipFee> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (MembershipFee f : fees) {
                ps.setString(1, f.getId());
                ps.setString(2, f.getCollectivityId());
                ps.setString(3, f.getLabel());
                ps.setDouble(4, f.getAmount());
                ps.setDate(5, Date.valueOf(f.getEligibleFrom()));
                ps.setString(6, f.getFrequency().name());
                ps.setString(7, f.getStatus().name());
                ps.addBatch();
            }
            ps.executeBatch();
            for (MembershipFee f : fees) result.add(findById(f.getId()).orElseThrow());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<MembershipFee> findById(String id) {
        String sql = """
                SELECT id, collectivity_id, label, amount, frequency, eligible_from, status
                FROM membership_fee WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(membershipFeeMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}