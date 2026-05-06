package com.collectivity.repository;

import com.collectivity.entity.Collectivity;
import com.collectivity.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
public class CollectivityMemberRepository {
    private final Connection connection;

    public void attachMemberToCollectivity(Collectivity collectivity, Member member) {
        String sql = """
                    insert into collectivity_member (id, member_id, collectivity_id)
                    values (?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, randomUUID().toString());
            ps.setString(2, member.getId());
            ps.setString(3, collectivity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
