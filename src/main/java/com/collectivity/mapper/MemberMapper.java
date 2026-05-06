package com.collectivity.mapper;

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
            String genderStr = rs.getString("gender");
            String occupationStr = rs.getString("occupation");
            return Member.builder()
                    .id(rs.getString("id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .birthDate(rs.getDate("birth_date") == null ? null : rs.getDate("birth_date").toLocalDate())
                    .gender(genderStr == null ? null : Gender.valueOf(genderStr))
                    .phoneNumber(rs.getString("phone_number"))
                    .email(rs.getString("email"))
                    .occupation(occupationStr == null ? null : MemberOccupation.valueOf(occupationStr))
                    .registrationFeePaid(rs.getBoolean("registration_fee_paid"))
                    .membershipDuesPaid(rs.getBoolean("membership_dues_paid"))
                    .address(rs.getString("address"))
                    .profession(rs.getString("profession"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}