package com.collectivity.repository;

import com.collectivity.entity.Member;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@Repository
public class MemberRepository {

    private final DataSource dataSource;

    public MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member m) {

        String sql = """
            INSERT INTO member (
                id, first_name, last_name, birth_date, gender,
                address, profession, phone_number, email, occupation,
                collectivity_id, registration_fee_paid, membership_dues_paid
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            m.id = Long.valueOf(UUID.randomUUID().toString());

            ps.setString(1, String.valueOf(m.id));
            ps.setString(2, m.firstName);
            ps.setString(3, m.lastName);
            ps.setDate(4, java.sql.Date.valueOf(m.birthDate));
            ps.setString(5, String.valueOf(m.gender));
            ps.setString(6, m.address);
            ps.setString(7, m.profession);
            ps.setLong(8, Long.parseLong(m.phoneNumber));
            ps.setString(9, m.email);
            ps.setString(10, String.valueOf(m.occupation));
            ps.setString(11, String.valueOf(m.collectivityId));
            ps.setBoolean(12, m.registrationFeePaid);
            ps.setBoolean(13, m.membershipDuesPaid);

            ps.executeUpdate();

            return m;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}