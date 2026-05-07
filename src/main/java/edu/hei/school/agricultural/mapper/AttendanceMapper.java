package edu.hei.school.agricultural.mapper;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public ActivityMemberAttendanceDTO toDTO(ActivityMemberAttendance attendance) {
        ActivityMemberAttendanceDTO dto = new ActivityMemberAttendanceDTO();
        dto.setId(attendance.getId());
        dto.setAttendanceStatus(attendance.getAttendanceStatus());

        ActivityMemberAttendanceDTO.MemberDescriptionDTO memberDesc =
                new ActivityMemberAttendanceDTO.MemberDescriptionDTO();
        memberDesc.setId(attendance.getMemberId());
        memberDesc.setFirstName(attendance.getMemberFirstName());
        memberDesc.setLastName(attendance.getMemberLastName());
        memberDesc.setEmail(attendance.getMemberEmail());
        memberDesc.setOccupation(attendance.getMemberOccupation());

        dto.setMemberDescription(memberDesc);
        return dto;
    }
}