package com.collectivity.repository;

import com.collectivity.entity.Collectivity;
import com.collectivity.entity.CollectivityStructure;
import com.collectivity.mapper.CollectivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final Connection connection;
    private final CollectivityMapper collectivityMapper;

    public List<Collectivity> saveAll(List<Collectivity> collectivities) {
        String sql = """
                INSERT INTO "collectivity"
                    (id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name              = excluded.name,
                    number            = excluded.number,
                    location          = excluded.location,
                    president_id      = excluded.president_id,
                    vice_president_id = excluded.vice_president_id,
                    treasurer_id      = excluded.treasurer_id,
                    secretary_id      = excluded.secretary_id
                """;
        List<Collectivity> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Collectivity c : collectivities) {
                CollectivityStructure s = c.getCollectivityStructure();
                ps.setString(1, c.getId());
                ps.setString(2, c.getName());
                if (c.getNumber() == null) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, c.getNumber());
                ps.setString(4, c.getLocation());
                ps.setString(5, s.getPresident().getId());
                ps.setString(6, s.getVicePresident().getId());
                ps.setString(7, s.getTreasurer().getId());
                ps.setString(8, s.getSecretary().getId());
                ps.addBatch();
            }
            ps.executeBatch();
            for (Collectivity c : collectivities) {
                result.add(findById(c.getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<Collectivity> findById(String id) {
        String sql = """
                SELECT id, name, number, location,
                       president_id, vice_president_id, treasurer_id, secretary_id
                FROM "collectivity" WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(collectivityMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean isNumberExists(Integer number) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM \"collectivity\" WHERE number = ?")) {
            ps.setInt(1, number);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isNameExists(String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM \"collectivity\" WHERE name = ?")) {
            ps.setString(1, name);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}