package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CollectivityActivityDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateCollectivityActivityDTO;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
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
    public ResponseEntity<?> getActivities(@PathVariable String id) {
        try {
            List<CollectivityActivityDTO> activities = activityService.getActivities(id);
            return ResponseEntity.ok(activities);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<?> addActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivityDTO> activities) {
        try {
            List<CollectivityActivityDTO> createdActivities = activityService.addActivities(id, activities);
            return ResponseEntity.ok(createdActivities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        try {
            List<ActivityMemberAttendanceDTO> attendances = attendanceService.getAttendance(id, activityId);
            return ResponseEntity.ok(attendances);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> confirmAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendanceDTO> attendances) {
        try {
            List<ActivityMemberAttendanceDTO> created =
                    attendanceService.confirmAttendance(id, activityId, attendances);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}