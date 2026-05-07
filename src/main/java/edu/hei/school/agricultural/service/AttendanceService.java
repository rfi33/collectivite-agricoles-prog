package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.mapper.AttendanceMapper;
import edu.hei.school.agricultural.repository.ActivityMemberAttendanceRepository;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public List<ActivityMemberAttendanceDTO> confirmAttendance(
            String collectivityId,
            String activityId,
            List<CreateActivityMemberAttendanceDTO> attendances) {

        CollectivityActivity activity = activityService.getActivityById(collectivityId, activityId);

        List<ActivityMemberAttendance> savedAttendances = attendances.stream()
                .map(dto -> {
                    String memberId = dto.getMemberIdentifier();

                    boolean alreadyConfirmed = attendanceRepository
                            .existsByActivityIdAndMemberIdAndAttendanceStatusNot(
                                    activityId,
                                    memberId,
                                    AttendanceStatus.UNDEFINED);
                    if (alreadyConfirmed) {
                        throw new BadRequestException(
                                "Attendance already confirmed for member: " + memberId);
                    }

                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new NotFoundException(
                                    "Member.id=" + memberId + " not found"));

                    boolean isFromCollectivity = collectivityRepository
                            .findAllByMemberId(memberId)
                            .stream()
                            .anyMatch(c -> c.getId().equals(collectivityId));


                    boolean isConcerned = isMemberConcernedByActivity(activity, member);

                    ActivityMemberAttendance entity = new ActivityMemberAttendance();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setActivityId(activityId);
                    entity.setMemberId(memberId);
                    entity.setAttendanceStatus(dto.getAttendanceStatus());
                    entity.setMemberFirstName(member.getFirstName() != null ? member.getFirstName() : "");
                    entity.setMemberLastName(member.getLastName() != null ? member.getLastName() : "");
                    entity.setMemberEmail(member.getEmail() != null ? member.getEmail() : "");
                    entity.setMemberOccupation(
                            member.getOccupation() != null ? member.getOccupation().name() : "");
                    entity.setFromCollectivity(isFromCollectivity);
                    entity.setConcerned(isConcerned);

                    return attendanceRepository.save(entity);
                })
                .collect(Collectors.toList());

        return savedAttendances.stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityMemberAttendanceDTO> getAttendance(String collectivityId, String activityId) {
        activityService.getActivityById(collectivityId, activityId);

        return attendanceRepository.findByActivityId(activityId).stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());
    }


    private boolean isMemberConcernedByActivity(CollectivityActivity activity, Member member) {
        if (activity.getMemberOccupationConcerned() == null
                || activity.getMemberOccupationConcerned().isEmpty()) {
            return true;
        }
        if (member.getOccupation() == null) {
            return false;
        }
        return activity.getMemberOccupationConcerned()
                .stream()
                .anyMatch(occ -> occ.name().equalsIgnoreCase(member.getOccupation().name()));
    }
}