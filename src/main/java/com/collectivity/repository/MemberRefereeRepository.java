package com.collectivity.repository;

import com.collectivity.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberRefereeRepository {
    private final Connection connection;

    public void attachMemberReferee(Member referee, Member refereed) {
        String sql = """
                INSERT INTO member_referee (id, member_referee_id, member_refereed_id)
                VALUES (?, ?, ?)
                ON CONFLICT (member_referee_id, member_refereed_id) DO NOTHING
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, referee.getId());
            ps.setString(3, refereed.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}