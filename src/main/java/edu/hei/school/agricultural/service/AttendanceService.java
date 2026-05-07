package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.mapper.AttendanceMapper;
import edu.hei.school.agricultural.repository.ActivityMemberAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final ActivityMemberAttendanceRepository attendanceRepository;
    private final CollectivityActivityService activityService;
    private final AttendanceMapper attendanceMapper;

    /**
     * POST /collectivities/{id}/activities/{activityId}/attendance
     * Une fois qu'un membre est marqué ATTENDED ou MISSING, on ne peut plus modifier.
     * Seul UNDEFINED peut être mis à jour.
     */
    public List<ActivityMemberAttendanceDTO> confirmAttendance(
            String collectivityId,
            String activityId,
            List<CreateActivityMemberAttendanceDTO> attendances) {

        // Vérifie que l'activité appartient bien à la collectivité
        CollectivityActivity activity = activityService.getActivityById(collectivityId, activityId);

        List<ActivityMemberAttendance> savedAttendances = attendances.stream()
                .map(dto -> {
                    // Vérifie qu'une présence non-UNDEFINED n'existe pas déjà pour ce membre
                    boolean alreadyConfirmed = attendanceRepository
                            .existsByActivityIdAndMemberIdAndAttendanceStatusNot(
                                    activityId,
                                    dto.getMemberIdentifier(),
                                    AttendanceStatus.UNDEFINED);
                    if (alreadyConfirmed) {
                        throw new BadRequestException(
                                "Attendance already confirmed for member: " + dto.getMemberIdentifier());
                    }

                    ActivityMemberAttendance entity = new ActivityMemberAttendance();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setActivityId(activityId);
                    entity.setMemberId(dto.getMemberIdentifier());
                    entity.setAttendanceStatus(dto.getAttendanceStatus());
                    // Les informations du membre seront vides ici — à enrichir via MemberRepository si nécessaire
                    entity.setMemberFirstName("");
                    entity.setMemberLastName("");
                    entity.setMemberEmail("");
                    entity.setMemberOccupation("");
                    entity.setFromCollectivity(true);
                    entity.setConcerned(true);

                    return attendanceRepository.save(entity);
                })
                .collect(Collectors.toList());

        return savedAttendances.stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /collectivities/{id}/activities/{activityId}/attendance
     */
    public List<ActivityMemberAttendanceDTO> getAttendance(String collectivityId, String activityId) {
        // Vérifie que l'activité appartient bien à la collectivité
        activityService.getActivityById(collectivityId, activityId);

        return attendanceRepository.findByActivityId(activityId).stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());
    }
}