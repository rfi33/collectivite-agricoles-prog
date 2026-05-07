package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.mapper.AttendanceMapper;
import edu.hei.school.agricultural.repository.ActivityMemberAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final ActivityMemberAttendanceRepository attendanceRepository;
    private final CollectivityActivityService activityService;
    private final AttendanceMapper attendanceMapper;
    
    public List<ActivityMemberAttendanceDTO> confirmAttendance(
            String collectivityId, 
            String activityId, 
            List<CreateActivityMemberAttendanceDTO> attendances) {

        CollectivityActivity activity = activityService.getActivityById(collectivityId, activityId);
        
        List<ActivityMemberAttendance> savedAttendances = attendances.stream()
            .map(dto -> {
                ActivityMemberAttendance entity = createAttendanceEntity(activityId, dto);
                entity.setId(generateId());
                return entity;
            })
            .peek(this::validateAttendanceNotConfirmed)
            .map(attendanceRepository::save)
            .collect(Collectors.toList());
        
        return savedAttendances.stream()
            .map(attendanceMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    private ActivityMemberAttendance createAttendanceEntity(String activityId, 
                                                             CreateActivityMemberAttendanceDTO dto) {
        ActivityMemberAttendance attendance = new ActivityMemberAttendance();
        attendance.setActivityId(activityId);
        attendance.setMemberId(dto.getMemberIdentifier());
        attendance.setAttendanceStatus(dto.getAttendanceStatus());
        attendance.setMemberFirstName("");
        attendance.setMemberLastName("");
        attendance.setMemberEmail("");
        attendance.setMemberOccupation("");
        attendance.setFromCollectivity(true);
        attendance.setConcerned(true);
        
        return attendance;
    }
    
    private void validateAttendanceNotConfirmed(ActivityMemberAttendance attendance) {
        if (attendanceRepository.existsByActivityIdAndMemberIdAndAttendanceStatusNot(
                attendance.getActivityId(), attendance.getMemberId(), AttendanceStatus.UNDEFINED)) {
            throw new AttendanceAlreadyConfirmedException(
                "Attendance status already confirmed for member: " + attendance.getMemberId());
        }
    }
    
    public List<ActivityMemberAttendanceDTO> getAttendance(String collectivityId, String activityId) {
        activityService.getActivityById(collectivityId, activityId);
        
        return attendanceRepository.findByActivityId(activityId).stream()
            .map(attendanceMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    private String generateId() {
        return "att_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}