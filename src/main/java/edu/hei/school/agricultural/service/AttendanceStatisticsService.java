package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.entity.ActivityMemberAttendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.repository.ActivityMemberAttendanceRepository;
import edu.hei.school.agricultural.repository.CollectivityActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AttendanceStatisticsService {

    private final ActivityMemberAttendanceRepository attendanceRepository;
    private final CollectivityActivityRepository activityRepository;

    public Double getMemberAssiduityPercentage(
            String collectivityId,
            String memberId,
            String memberOccupation) {


        List<CollectivityActivity> concernedActivities = activityRepository
                .findByCollectivityId(collectivityId)
                .stream()
                .filter(activity -> isMemberConcerned(activity, memberOccupation))
                .toList();

        if (concernedActivities.isEmpty()) {
            return null;
        }

        long attendedCount = concernedActivities.stream()
                .filter(activity -> {
                    return attendanceRepository
                            .findByActivityIdAndMemberId(activity.getId(), memberId)
                            .map(a -> a.getAttendanceStatus() == AttendanceStatus.ATTENDED)
                            .orElse(false);
                })
                .count();

        return round((double) attendedCount / concernedActivities.size() * 100.0);
    }

    public Double getCollectivityOverallAssiduityPercentage(
            String collectivityId,
            List<MemberIdWithOccupation> memberIds) {

        if (memberIds == null || memberIds.isEmpty()) {
            return null;
        }

        List<Double> memberRates = memberIds.stream()
                .map(m -> getMemberAssiduityPercentage(collectivityId, m.memberId(), m.occupation()))
                .filter(rate -> rate != null)
                .toList();

        if (memberRates.isEmpty()) {
            return null;
        }

        double average = memberRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return round(average);
    }


    private boolean isMemberConcerned(CollectivityActivity activity, String memberOccupation) {
        if (activity.getMemberOccupationConcerned() == null
                || activity.getMemberOccupationConcerned().isEmpty()) {

            return true;
        }
        if (memberOccupation == null) {
            return false;
        }
        return activity.getMemberOccupationConcerned()
                .stream()
                .anyMatch(occ -> occ.name().equalsIgnoreCase(memberOccupation));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record MemberIdWithOccupation(String memberId, String occupation) {}
}