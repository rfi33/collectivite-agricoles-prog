// dto/ActivityMemberAttendanceDTO.java
package edu.hei.school.agricultural.controller.dto;

import edu.hei.school.agricultural.entity.AttendanceStatus;
import lombok.Data;

@Data
public class ActivityMemberAttendanceDTO {
    private String id;
    private MemberDescriptionDTO memberDescription;
    private AttendanceStatus attendanceStatus;
    
    @Data
    public static class MemberDescriptionDTO {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String occupation;
    }
}