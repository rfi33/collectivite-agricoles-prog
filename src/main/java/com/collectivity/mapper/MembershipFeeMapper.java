package com.collectivity.mapper;

import com.collectivity.entity.ActivityStatus;
import com.collectivity.entity.Frequency;
import com.collectivity.entity.MembershipFee;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MembershipFeeMapper {

    public MembershipFee mapFromResultSet(ResultSet rs) throws SQLException {
        String freq   = rs.getString("frequency");
        String status = rs.getString("status");
        return MembershipFee.builder()
                .id(rs.getString("id"))
                .label(rs.getString("label"))
                .amount(rs.getDouble("amount"))
                .frequency(freq   == null ? null : Frequency.valueOf(freq))
                .eligibleFrom(rs.getDate("eligible_from") == null ? null : rs.getDate("eligible_from").toLocalDate())
                .status(status == null ? null : ActivityStatus.valueOf(status))
                .collectivityId(rs.getString("collectivity_id"))
                .build();
    }
}