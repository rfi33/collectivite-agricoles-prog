package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.CollectivityActivityDTO;
import edu.hei.school.agricultural.service.AttendanceService;
import edu.hei.school.agricultural.service.CollectivityActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
@RequiredArgsConstructor
public class CollectivityActivityController {
    
    private final CollectivityActivityService activityService;
    private final AttendanceService attendanceService;
    
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<CollectivityActivityDTO>> getActivities(@PathVariable String id) {
        List<CollectivityActivityDTO> activities = activityService.getActivities(id);
        return ResponseEntity.ok(activities);
    }
    
    @PostMapping("/{id}/activities")
    public ResponseEntity<List<CollectivityActivityDTO>> addActivities(
            @PathVariable String id,
            @Valid @RequestBody List<CreateCollectivityActivityDTO> activities) {
        List<CollectivityActivityDTO> createdActivities = activityService.addActivities(id, activities);
        return ResponseEntity.ok(createdActivities);
    }
    
    @GetMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendanceDTO>> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        List<ActivityMemberAttendanceDTO> attendances = attendanceService.getAttendance(id, activityId);
        return ResponseEntity.ok(attendances);
    }
    
    @PostMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendanceDTO>> confirmAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @Valid @RequestBody List<CreateActivityMemberAttendanceDTO> attendances) {
        List<ActivityMemberAttendanceDTO> createdAttendances = 
            attendanceService.confirmAttendance(id, activityId, attendances);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttendances);
    }
}