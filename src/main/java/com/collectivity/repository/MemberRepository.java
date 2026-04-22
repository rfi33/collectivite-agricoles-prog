package com.collectivity.repository;

import com.collectivity.entity.Gender;
import com.collectivity.entity.Member;
import com.collectivity.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final Connection connection;
<<<<<<< HEAD

    // Spring injects the Connection bean from DataSource via constructor
    public MemberRepository(Connection connection) {
        this.connection = connection;
    }

    // ------------------------------------------------------------------
    // Save a new member
    // ------------------------------------------------------------------
    public Member save(Member m) {
        String sql = """
            INSERT INTO members (
                id, first_name, last_name, birth_date, gender,
                address, profession, phone_number, email, occupation,
                collectivity_id, join_date,
                registration_fee_paid, membership_dues_paid
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            String id = UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, m.firstName);
            ps.setString(3, m.lastName);
            ps.setDate(4, Date.valueOf(m.birthDate));
            ps.setString(5, m.gender.name());
            ps.setString(6, m.address);
            ps.setString(7, m.profession);
            ps.setString(8, m.phoneNumber);
            ps.setString(9, m.email);
            ps.setString(10, m.occupation.name());
            ps.setString(11, m.collectivityId);
            ps.setDate(12, Date.valueOf(LocalDate.now()));
            ps.setBoolean(13, true);
            ps.setBoolean(14, true);
            ps.executeUpdate();

            m.id = id;
            return m;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save member", e);
        }
    }

    // ------------------------------------------------------------------
    // Save referees with their relation for a member (B-2)
    // ------------------------------------------------------------------
    public void saveReferees(String memberId, List<String> refereeIds, List<String> relations) {
        String sql = "INSERT INTO member_referees (member_id, referee_id, relation) VALUES (?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < refereeIds.size(); i++) {
                ps.setString(1, memberId);
                ps.setString(2, refereeIds.get(i));
                ps.setString(3, relations.get(i));
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save referees", e);
        }
    }

    // ------------------------------------------------------------------
    // Find a member by ID
    // ------------------------------------------------------------------
    public Member findById(String id) {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find member id=" + id, e);
        }
    }

    // ------------------------------------------------------------------
    // Find all members of a collectivity
    // ------------------------------------------------------------------
    public List<Member> findByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM members WHERE collectivity_id = ?";
        List<Member> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find members for collectivity id=" + collectivityId, e);
        }
    }

    // ------------------------------------------------------------------
    // Find hydrated referees of a member (used in the response)
    // ------------------------------------------------------------------
    public List<Member> findRefereesByMemberId(String memberId) {
        String sql = """
            SELECT m.* FROM members m
            INNER JOIN member_referees mr ON m.id = mr.referee_id
            WHERE mr.member_id = ?
        """;
        List<Member> referees = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                referees.add(mapRow(rs));
            }
            return referees;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find referees for member id=" + memberId, e);
        }
    }

    // ------------------------------------------------------------------
    // Check that a referee is CONFIRMED (not JUNIOR) with seniority > 90 days
    // Condition B-2
    // ------------------------------------------------------------------
    public boolean isConfirmedWithSeniority(String memberId) {
        String sql = "SELECT occupation, join_date FROM members WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String occ = rs.getString("occupation");
                LocalDate joinDate = rs.getDate("join_date").toLocalDate();

                boolean isConfirmed = !occ.equals(MemberOccupation.JUNIOR.name());
                boolean hasEnoughSeniority = joinDate.isBefore(LocalDate.now().minusDays(90));

                return isConfirmed && hasEnoughSeniority;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check referee seniority id=" + memberId, e);
        }
    }

    // ------------------------------------------------------------------
    // Get the collectivity ID of a member
    // Used in B-2 to count internal vs external referees
    // ------------------------------------------------------------------
    public String getCollectivityIdOf(String memberId) {
        String sql = "SELECT collectivity_id FROM members WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("collectivity_id");
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get collectivity of member id=" + memberId, e);
        }
    }

    // ------------------------------------------------------------------
    // Count members with seniority >= 6 months among a given list of IDs
    // Condition A: at least 5 founding members must meet this requirement
    // ------------------------------------------------------------------
    public int countMembersWithSixMonthsSeniority(List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) return 0;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < memberIds.size(); i++) {
            placeholders.append(i == 0 ? "?" : ",?");
=======

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
>>>>>>> f574a21c753cbd9cad632d47cfa12462e9cc5088
        }
    }

<<<<<<< HEAD
        String sql = "SELECT COUNT(*) FROM members WHERE id IN ("
                + placeholders
                + ") AND join_date <= CURRENT_DATE - INTERVAL '6 months'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < memberIds.size(); i++) {
                ps.setString(i + 1, memberIds.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count members with 6 months seniority", e);
        }
    }

    // ------------------------------------------------------------------
    // Map a ResultSet row to a Member entity
    // ------------------------------------------------------------------
    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.id             = rs.getString("id");
        m.firstName      = rs.getString("first_name");
        m.lastName       = rs.getString("last_name");
        m.birthDate      = rs.getDate("birth_date").toLocalDate();
        m.gender         = Gender.valueOf(rs.getString("gender"));
        m.address        = rs.getString("address");
        m.profession     = rs.getString("profession");
        m.phoneNumber    = rs.getString("phone_number");
        m.email          = rs.getString("email");
        m.occupation     = MemberOccupation.valueOf(rs.getString("occupation"));
        m.collectivityId = rs.getString("collectivity_id");
        m.joinDate       = rs.getDate("join_date").toLocalDate();
=======
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
>>>>>>> f574a21c753cbd9cad632d47cfa12462e9cc5088
        return m;
    }
}