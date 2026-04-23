package com.collectivity.repository;

import com.collectivity.entity.Collectivity;
import com.collectivity.entity.Member;
import com.collectivity.entity.Specialization;
import org.postgresql.util.PGobject;
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
                id, location, specialization, federation_approval,
                president_id, vice_president_id, treasurer_id, secretary_id
            ) VALUES (?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String id = UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, c.location);
            ps.setObject(3, c.specialization != null
                    ? toPGEnum("specialization", c.specialization.name()) : null);
            ps.setBoolean(4, c.federationApproval);
            ps.setString(5, c.president     != null ? c.president.id     : null);
            ps.setString(6, c.vicePresident != null ? c.vicePresident.id : null);
            ps.setString(7, c.treasurer     != null ? c.treasurer.id     : null);
            ps.setString(8, c.secretary     != null ? c.secretary.id     : null);
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
            if (rs.next()) return mapRow(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collectivity id=" + id, e);
        }
    }

    public Collectivity findByIdWithStructure(String id) {
        String sql = """
            SELECT
                c.id, c.name, c.number, c.location, c.specialization, c.federation_approval,
                p.id  AS p_id,  p.first_name  AS p_fn,  p.last_name  AS p_ln,
                vp.id AS vp_id, vp.first_name AS vp_fn, vp.last_name AS vp_ln,
                t.id  AS t_id,  t.first_name  AS t_fn,  t.last_name  AS t_ln,
                s.id  AS s_id,  s.first_name  AS s_fn,  s.last_name  AS s_ln
            FROM collectivities c
            LEFT JOIN members p  ON c.president_id      = p.id
            LEFT JOIN members vp ON c.vice_president_id = vp.id
            LEFT JOIN members t  ON c.treasurer_id      = t.id
            LEFT JOIN members s  ON c.secretary_id      = s.id
            WHERE c.id = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            Collectivity c = mapRow(rs);
            c.president     = mapStructureMember(rs, "p");
            c.vicePresident = mapStructureMember(rs, "vp");
            c.treasurer     = mapStructureMember(rs, "t");
            c.secretary     = mapStructureMember(rs, "s");
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collectivity with structure id=" + id, e);
        }
    }

    public boolean existsByNameOrNumberExcludingId(String name, Integer number, String excludeId) {
        String sql = """
            SELECT 1 FROM collectivities
            WHERE (name = ? OR number = ?)
              AND id <> ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setObject(2, number, Types.INTEGER);
            ps.setString(3, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check name/number uniqueness", e);
        }
    }

    public Collectivity updateInformations(String id, String name, Integer number) {
        String sql = "UPDATE collectivities SET name = ?, number = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setObject(2, number, Types.INTEGER);
            ps.setString(3, id);
            ps.executeUpdate();
            return findByIdWithStructure(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update informations for collectivity id=" + id, e);
        }
    }

    private Collectivity mapRow(ResultSet rs) throws SQLException {
        Collectivity c = new Collectivity();
        c.id                 = rs.getString("id");
        c.name               = rs.getString("name");
        int num              = rs.getInt("number");
        c.number             = rs.wasNull() ? null : num;
        c.location           = rs.getString("location");
        c.federationApproval = rs.getBoolean("federation_approval");
        String spec          = rs.getString("specialization");
        c.specialization     = spec != null ? Specialization.valueOf(spec) : null;
        return c;
    }

    private Member mapStructureMember(ResultSet rs, String prefix) throws SQLException {
        String id = rs.getString(prefix + "_id");
        if (id == null) return null;
        Member m = new Member();
        m.id        = id;
        m.firstName = rs.getString(prefix + "_fn");
        m.lastName  = rs.getString(prefix + "_ln");
        return m;
    }

    private PGobject toPGEnum(String typeName, String value) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(typeName);
        pgObject.setValue(value);
        return pgObject;
    }
}