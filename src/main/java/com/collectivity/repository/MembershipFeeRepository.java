package com.collectivity.repository;

import com.collectivity.entity.ActivityStatus;
import com.collectivity.entity.Frequency;
import com.collectivity.entity.MembershipFee;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MembershipFeeRepository {

    private final Connection connection;

    public MembershipFeeRepository(Connection connection) {
        this.connection = connection;
    }

    public List<MembershipFee> findByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM membership_fees WHERE collectivity_id = ?";
        List<MembershipFee> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find membership fees for collectivity id=" + collectivityId, e);
        }
    }

    public MembershipFee save(MembershipFee fee) {
        String sql = """
            INSERT INTO membership_fees (
                id, collectivity_id, eligible_from, frequency, amount, label, status
            ) VALUES (?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String id = UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, fee.collectivityId);
            ps.setDate(3, Date.valueOf(fee.eligibleFrom));
            ps.setString(4, fee.frequency.name());
            ps.setBigDecimal(5, fee.amount);
            ps.setString(6, fee.label);
            ps.setString(7, fee.status.name());
            ps.executeUpdate();
            fee.id = id;
            return fee;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save membership fee for collectivity id=" + fee.collectivityId, e);
        }
    }

    private MembershipFee mapRow(ResultSet rs) throws SQLException {
        MembershipFee fee = new MembershipFee();
        fee.id             = rs.getString("id");
        fee.collectivityId = rs.getString("collectivity_id");
        fee.eligibleFrom   = rs.getDate("eligible_from").toLocalDate();
        fee.frequency      = Frequency.valueOf(rs.getString("frequency"));
        fee.amount         = rs.getBigDecimal("amount");
        fee.label          = rs.getString("label");
        fee.status         = ActivityStatus.valueOf(rs.getString("status"));
        return fee;
    }
}