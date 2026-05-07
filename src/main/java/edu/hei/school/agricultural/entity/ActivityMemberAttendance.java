package edu.hei.school.agricultural.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMemberAttendance {
    private String id;
    private String activityId;
    private String memberId;
    private AttendanceStatus attendanceStatus;
    private String memberFirstName;
    private String memberLastName;
    private String memberEmail;
    private String memberOccupation;
    private boolean isFromCollectivity;
    private boolean isConcerned;
}