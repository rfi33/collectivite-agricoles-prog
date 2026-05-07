package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityInformation;
import edu.hei.school.agricultural.controller.dto.CollectivityLocalStatistics;
import edu.hei.school.agricultural.controller.dto.CollectivityOverallStatistics;
import edu.hei.school.agricultural.controller.dto.MemberDescription;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final CollectivityRepository collectivityRepository;

    /**
     * G - Statistiques locales d'une collectivité :
     * montant encaissé et montant impayé potentiel par membre actif sur une période.
     */
    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id=" + collectivityId + " not found"));

        List<Map<String, Object>> rawStats =
                statisticsRepository.getMemberStatsByCollectivityAndPeriod(collectivityId, from, to);

        return rawStats.stream()
                .map(row -> CollectivityLocalStatistics.builder()
                        .memberDescription(MemberDescription.builder()
                                .id((String) row.get("member_id"))
                                .firstName((String) row.get("first_name"))
                                .lastName((String) row.get("last_name"))
                                .email((String) row.get("email"))
                                .occupation((String) row.get("occupation"))
                                .build())
                        .earnedAmount((Double) row.get("earned_amount"))
                        .unpaidAmount((Double) row.get("unpaid_amount"))
                        .build())
                .toList();
    }

    /**
     * H - Statistiques globales de toutes les collectivités :
     * % membres à jour dans leurs cotisations + nombre de nouveaux adhérents sur la période.
     */
    public List<CollectivityOverallStatistics> getOverallStatistics(LocalDate from, LocalDate to) {

        List<Map<String, Object>> rawStats =
                statisticsRepository.getOverallStatisticsForAllCollectivities(from, to);

        return rawStats.stream()
                .map(row -> {
                    CollectivityInformation info = new CollectivityInformation(
                            (String) row.get("collectivity_name"),
                            toInteger(row.get("collectivity_number"))
                    );

                    return CollectivityOverallStatistics.builder()
                            .collectivityInformation(info)
                            .newMembersNumber((Integer) row.get("new_members_number"))
                            .overallMemberCurrentDuePercentage(
                                    (Double) row.get("overall_member_current_due_percentage"))
                            .build();
                })
                .toList();
    }

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer i) return i;
        if (obj instanceof Number n) return n.intValue();
        return null;
    }
}
