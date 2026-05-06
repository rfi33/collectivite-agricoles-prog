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
        List<Member> result = new ArrayList<>();
        String sql = """
                INSERT INTO "member" (id, first_name, last_name, birth_date, gender, address, profession,
                                      phone_number, email, occupation, registration_fee_paid, membership_dues_paid)
                VALUES (?, ?, ?, ?, ?::gender, ?, ?, ?, ?, ?::member_occupation, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    first_name = excluded.first_name, last_name = excluded.last_name,
                    birth_date = excluded.birth_date, gender = excluded.gender,
                    phone_number = excluded.phone_number, email = excluded.email,
                    address = excluded.address, profession = excluded.profession,
                    occupation = excluded.occupation
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Member member : members) {
                if (member.getId() == null) member.setId(UUID.randomUUID().toString());
                ps.setString(1, member.getId());
                ps.setString(2, member.getFirstName());
                ps.setString(3, member.getLastName());
                ps.setDate(4, Date.valueOf(member.getBirthDate()));
                ps.setObject(5, member.getGender() == null ? null : member.getGender().name());
                ps.setString(6, member.getAddress());
                ps.setString(7, member.getProfession());
                ps.setString(8, member.getPhoneNumber());
                ps.setString(9, member.getEmail());
                ps.setObject(10, member.getOccupation() == null ? "JUNIOR" : member.getOccupation().name());
                ps.setBoolean(11, Boolean.TRUE.equals(member.getRegistrationFeePaid()));
                ps.setBoolean(12, Boolean.TRUE.equals(member.getMembershipDuesPaid()));
                ps.addBatch();
            }
            ps.executeBatch();
            for (Member member : members) {
                attachCollectivityMember(member);
                attachRefereeMember(member);
                result.add(findById(member.getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void attachCollectivityMember(Member member) {
        if (member.getCollectivities() == null) return;
        for (Collectivity c : member.getCollectivities()) {
            collectivityMemberRepository.attachMemberToCollectivity(c, member);
        }
    }

    private void attachRefereeMember(Member member) {
        if (member.getReferees() == null) return;
        for (Member referee : member.getReferees()) {
            memberRefereeRepository.attachMemberReferee(referee, member);
        }
    }

    public Optional<Member> findById(String id) {
        String sql = """
                SELECT id, first_name, last_name, birth_date, gender, phone_number, email,
                       address, profession, occupation, registration_fee_paid, membership_dues_paid
                FROM "member" WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Member member = memberMapper.mapFromResultSet(rs);
                member.setReferees(findRefereesByMemberId(member.getId()));
                return Optional.of(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Member> findAllByCollectivity(Collectivity collectivity) {
        List<Member> list = new ArrayList<>();
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date, m.gender,
                       m.phone_number, m.email, m.address, m.profession, m.occupation,
                       m.registration_fee_paid, m.membership_dues_paid
                FROM "member" m
                JOIN collectivity_member cm ON m.id = cm.member_id
                WHERE cm.collectivity_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivity.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member member = memberMapper.mapFromResultSet(rs);
                member.setReferees(findRefereesByMemberId(member.getId()));
                member.addCollectivity(collectivity);
                list.add(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private List<Member> findRefereesByMemberId(String memberId) {
        List<Member> list = new ArrayList<>();
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date, m.gender,
                       m.phone_number, m.email, m.address, m.profession, m.occupation,
                       m.registration_fee_paid, m.membership_dues_paid
                FROM "member" m
                JOIN member_referee mr ON m.id = mr.member_referee_id
                WHERE mr.member_refereed_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(memberMapper.mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}