package com.collectivity.repository;

import com.collectivity.entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.UUID;

@Repository
public class CollectivityRepository {

    private final Connection connection;

    public CollectivityRepository(Connection connection) {
        this.connection = connection;
    }
    public Collectivity save(Collectivity c) {
        String sql = """
            INSERT INTO collectivities (
                id, location, federation_approval,
                president_id, vice_president_id, treasurer_id, secretary_id
            ) VALUES (?,?,?,?,?,?,?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            String id = UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, c.location);
            ps.setBoolean(3, c.federationApproval);
            ps.setString(4, c.president     != null ? c.president.id     : null);
            ps.setString(5, c.vicePresident != null ? c.vicePresident.id : null);
            ps.setString(6, c.treasurer     != null ? c.treasurer.id     : null);
            ps.setString(7, c.secretary     != null ? c.secretary.id     : null);
            ps.executeUpdate();

            c.id = id;
            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save collectivity", e);
        }
    }

    public void saveMembers(String collectivityId, List<String> memberIds) {
        String sql = "INSERT INTO collectivity_members (collectivity_id, member_id) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (String memberId : memberIds) {
                ps.setString(1, collectivityId);
                ps.setString(2, memberId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to link members to collectivity", e);
        }
    }

    public Collectivity findById(String id) {
        String sql = "SELECT * FROM collectivities WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collectivity id=" + id, e);
        }
    }

    private Collectivity mapRow(ResultSet rs) throws SQLException {
        Collectivity c = new Collectivity();
        c.id                 = rs.getString("id");
        c.location           = rs.getString("location");
        c.federationApproval = rs.getBoolean("federation_approval");
        return c;
    }
}