package com.collectivity.repository;

import com.collectivity.entity.Collectivity;
import com.collectivity.entity.Member;
import com.collectivity.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final Connection connection;
    private final MemberMapper memberMapper;
    private final CollectivityMemberRepository collectivityMemberRepository;
    private final MemberRefereeRepository memberRefereeRepository;

    public List<Member> saveAll(List<Member> members) {
        String sql = """
                INSERT INTO "member"
                    (id, first_name, last_name, birth_date, gender, address, profession,
                     phone_number, email, occupation, registration_fee_paid, membership_dues_paid)
                VALUES (?, ?, ?, ?, ?::gender, ?, ?, ?, ?, ?::member_occupation, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    first_name = excluded.first_name,
                    last_name  = excluded.last_name,
                    birth_date = excluded.birth_date,
                    gender     = excluded.gender,
                    phone_number = excluded.phone_number,
                    email      = excluded.email,
                    address    = excluded.address,
                    profession = excluded.profession,
                    occupation = excluded.occupation
                """;
        List<Member> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Member m : members) {
                if (m.getId() == null) m.setId(UUID.randomUUID().toString());
                ps.setString(1, m.getId());
                ps.setString(2, m.getFirstName());
                ps.setString(3, m.getLastName());
                ps.setDate(4, Date.valueOf(m.getBirthDate()));
                ps.setObject(5, m.getGender() == null ? "JUNIOR" : m.getGender().name());
                ps.setString(6, m.getAddress());
                ps.setString(7, m.getProfession());
                ps.setString(8, m.getPhoneNumber());
                ps.setString(9, m.getEmail());
                ps.setObject(10, m.getOccupation() == null ? "JUNIOR" : m.getOccupation().name());
                ps.setBoolean(11, Boolean.TRUE.equals(m.getRegistrationFeePaid()));
                ps.setBoolean(12, Boolean.TRUE.equals(m.getMembershipDuesPaid()));
                ps.addBatch();
            }
            ps.executeBatch();
            for (Member m : members) {
                if (m.getCollectivities() != null) {
                    for (Collectivity c : m.getCollectivities()) {
                        collectivityMemberRepository.attach(c, m);
                    }
                }
                if (m.getReferees() != null) {
                    for (Member ref : m.getReferees()) {
                        memberRefereeRepository.attach(ref, m);
                    }
                }
                result.add(findById(m.getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<Member> findById(String id) {
        String sql = """
                SELECT id, first_name, last_name, birth_date, gender, phone_number,
                       email, address, profession, occupation, registration_fee_paid, membership_dues_paid
                FROM "member" WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Member m = memberMapper.mapFromResultSet(rs);
                m.setReferees(findRefereesByMemberId(m.getId()));
                return Optional.of(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Member> findAllByCollectivity(Collectivity collectivity) {
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date, m.gender,
                       m.phone_number, m.email, m.address, m.profession, m.occupation,
                       m.registration_fee_paid, m.membership_dues_paid
                FROM "member" m
                JOIN collectivity_member cm ON m.id = cm.member_id
                WHERE cm.collectivity_id = ?
                """;
        List<Member> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivity.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member m = memberMapper.mapFromResultSet(rs);
                m.setReferees(findRefereesByMemberId(m.getId()));
                m.addCollectivity(collectivity);
                list.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private List<Member> findRefereesByMemberId(String memberId) {
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date, m.gender,
                       m.phone_number, m.email, m.address, m.profession, m.occupation,
                       m.registration_fee_paid, m.membership_dues_paid
                FROM "member" m
                JOIN member_referee mr ON m.id = mr.member_referee_id
                WHERE mr.member_refereed_id = ?
                """;
        List<Member> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(memberMapper.mapFromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}