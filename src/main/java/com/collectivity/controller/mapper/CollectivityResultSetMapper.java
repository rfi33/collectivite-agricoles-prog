package com.collectivity.controller.mapper;

import com.collectivity.entity.Collectivity;
import com.collectivity.entity.CollectivityStructure;
import com.collectivity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class CollectivityResultSetMapper {
    private final MemberRepository memberRepository;

    public Collectivity mapFromResultSet(ResultSet rs) throws SQLException {
        String presidentId = rs.getString("president_id");
        String vpId        = rs.getString("vice_president_id");
        String treasurerId = rs.getString("treasurer_id");
        String secretaryId = rs.getString("secretary_id");

        Collectivity c = Collectivity.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .number(rs.getObject("number") == null ? null : rs.getInt("number"))
                .location(rs.getString("location"))
                .collectivityStructure(CollectivityStructure.builder()
                        .president  (presidentId  == null ? null : memberRepository.findById(presidentId).orElse(null))
                        .vicePresident(vpId       == null ? null : memberRepository.findById(vpId).orElse(null))
                        .treasurer  (treasurerId  == null ? null : memberRepository.findById(treasurerId).orElse(null))
                        .secretary  (secretaryId  == null ? null : memberRepository.findById(secretaryId).orElse(null))
                        .build())
                .build();
        c.addMembers(memberRepository.findAllByCollectivity(c));
        return c;
    }
}