package com.collectivity.repository;

import com.collectivity.entity.Gender;
import com.collectivity.entity.Member;
import com.collectivity.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final Connection connection;

    public MemberRepository(Connection connection) {
        this.connection = connection;
    }

    // ─── SAVE (INSERT member + referees in one transaction) ───────────────────

    public Member save(Member member, List<String> refereeIds) throws SQLException {
        connection.setAutoCommit(false);
        try {
            insertMember(member);
            saveReferees(String.valueOf(member.getId()), refereeIds);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        return member;
    }

    private void insertMember(Member member) throws SQLException {
        String sql = """
                INSERT INTO member
                    (id, first_name, last_name, birth_date, gender, address,
                     profession, phone_number, email, occupation, adhesion_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(member.getId()));
            ps.setString(2, member.getFirstName());
            ps.setString(3, member.getLastName());
            ps.setDate(4, Date.valueOf(member.getBirthDate()));
            ps.setString(5, member.getGender().name());
            ps.setString(6, member.getAddress());
            ps.setString(7, member.getProfession());
            ps.setString(8, member.getPhoneNumber());
            ps.setString(9, member.getEmail());
            ps.setString(10, member.getOccupation().name());
            ps.executeUpdate();
        }
    }

    private void saveReferees(String memberId, List<String> refereeIds) throws SQLException {
        if (refereeIds == null || refereeIds.isEmpty()) return;
        String sql = "INSERT INTO member_referee (member_id, referee_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String refereeId : refereeIds) {
                ps.setString(1, memberId);
                ps.setString(2, refereeId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ─── FIND BY ID ──────────────────────────────────────────────────────────

    public Optional<Member> findById(String id) throws SQLException {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ─── FIND ALL BY IDS ─────────────────────────────────────────────────────

    public List<Member> findAllByIds(List<String> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM member WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append(i == 0 ? "?" : ", ?");
        }
        sql.append(")");

        List<Member> members = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setString(i + 1, ids.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRow(rs));
                }
            }
        }
        return members;
    }

    // ─── FIND REFEREES OF A MEMBER ────────────────────────────────────────────

    public List<Member> findRefereesByMemberId(String memberId) throws SQLException {
        String sql = """
                SELECT m.* FROM member m
                JOIN member_referee mr ON m.id = mr.referee_id
                WHERE mr.member_id = ?
                """;
        List<Member> referees = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    referees.add(mapRow(rs));
                }
            }
        }
        return referees;
    }

    // ─── COUNT MEMBERS WITH SENIORITY >= N DAYS ───────────────────────────────

    public int countWithSeniority(List<String> memberIds, int minDays) throws SQLException {
        if (memberIds == null || memberIds.isEmpty()) return 0;

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM member WHERE id IN (");
        for (int i = 0; i < memberIds.size(); i++) {
            sql.append(i == 0 ? "?" : ", ?");
        }
        sql.append(") AND adhesion_date <= CURRENT_DATE - INTERVAL '")
                .append(minDays)
                .append(" days'");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < memberIds.size(); i++) {
                ps.setString(i + 1, memberIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // ─── CHECK IF A MEMBER BELONGS TO A COLLECTIVITY ─────────────────────────

    public boolean belongsToCollectivity(String memberId,
                                         String collectivityId) throws SQLException {
        String sql = """
                SELECT 1 FROM collectivity_member
                WHERE member_id = ? AND collectivity_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ─── MAPPER ──────────────────────────────────────────────────────────────

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(Long.valueOf(rs.getString("id")));
        m.setFirstName(rs.getString("first_name"));
        m.setLastName(rs.getString("last_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setGender(Gender.valueOf(rs.getString("gender")));
        m.setAddress(rs.getString("address"));
        m.setProfession(rs.getString("profession"));
        m.setPhoneNumber(rs.getString("phone_number"));
        m.setEmail(rs.getString("email"));
        m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
        return m;
    }
}