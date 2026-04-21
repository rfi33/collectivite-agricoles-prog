package repository;

import config.DBConfig;
import entity.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberRepository {

    public Member save(Member m, String collectivityId) {
        try (Connection conn = DBConfig.getConnection()) {

            String id = UUID.randomUUID().toString();

            String sql = """
                INSERT INTO members(
                    id, first_name, last_name, birth_date, gender,
                    address, profession, phone_number, email, occupation,
                    collectivity_id, registration_fee_paid, membership_dues_paid
                )
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, m.firstName);
            ps.setString(3, m.lastName);
            ps.setDate(4, Date.valueOf(m.birthDate));
            ps.setString(5, m.gender);
            ps.setString(6, m.address);
            ps.setString(7, m.profession);
            ps.setString(8, m.phoneNumber);
            ps.setString(9, m.email);
            ps.setString(10, m.occupation);
            ps.setString(11, collectivityId);
            ps.setBoolean(12, true);
            ps.setBoolean(13, true);

            ps.executeUpdate();

            m.id = id;
            return m;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findByCollectivity(String collectivityId) {
        List<Member> list = new ArrayList<>();

        try (Connection conn = DBConfig.getConnection()) {

            String sql = "SELECT * FROM members WHERE collectivity_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Member m = new Member();
                m.id = rs.getString("id");
                m.firstName = rs.getString("first_name");
                m.lastName = rs.getString("last_name");
                list.add(m);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}