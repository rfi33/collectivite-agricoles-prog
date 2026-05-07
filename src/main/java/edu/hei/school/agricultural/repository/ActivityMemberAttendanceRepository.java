package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ActivityMemberAttendanceRepository {
    
    private final Map<String, ActivityMemberAttendance> attendances = new ConcurrentHashMap<>();
    
    public ActivityMemberAttendance save(ActivityMemberAttendance attendance) {
        attendances.put(attendance.getId(), attendance);
        return attendance;
    }
    
    public Optional<ActivityMemberAttendance> findById(String id) {
        return Optional.ofNullable(attendances.get(id));
    }
    
    public List<ActivityMemberAttendance> findByActivityId(String activityId) {
        return attendances.values().stream()
            .filter(attendance -> attendance.getActivityId().equals(activityId))
            .collect(Collectors.toList());
    }
    
    public Optional<ActivityMemberAttendance> findByActivityIdAndMemberId(String activityId, String memberId) {
        return attendances.values().stream()
            .filter(attendance -> attendance.getActivityId().equals(activityId) 
                    && attendance.getMemberId().equals(memberId))
            .findFirst();
    }
    
    public boolean existsByActivityIdAndMemberIdAndAttendanceStatusNot(String activityId, 
                                                                        String memberId, 
                                                                        AttendanceStatus status) {
        return findByActivityIdAndMemberId(activityId, memberId)
            .map(attendance -> attendance.getAttendanceStatus() != status)
            .orElse(false);
    }
    
    public List<ActivityMemberAttendance> findAll() {
        return new ArrayList<>(attendances.values());
    }
}