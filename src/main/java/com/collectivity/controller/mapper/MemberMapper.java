package com.collectivity.controller.mapper;

import com.collectivity.entity.Gender;
import com.collectivity.entity.Member;
import com.collectivity.entity.MemberOccupation;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MemberMapper {

    public Member mapFromResultSet(ResultSet rs) {
        try {
            String g = rs.getString("gender");
            String o = rs.getString("occupation");
            return Member.builder()
                    .id(rs.getString("id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .birthDate(rs.getDate("birth_date") == null ? null : rs.getDate("birth_date").toLocalDate())
                    .gender(g == null ? null : Gender.valueOf(g))
                    .phoneNumber(rs.getString("phone_number"))
                    .email(rs.getString("email"))
                    .address(rs.getString("address"))
                    .profession(rs.getString("profession"))
                    .occupation(o == null ? null : MemberOccupation.valueOf(o))
                    .registrationFeePaid(rs.getBoolean("registration_fee_paid"))
                    .membershipDuesPaid(rs.getBoolean("membership_dues_paid"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}