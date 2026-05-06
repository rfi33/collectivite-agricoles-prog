package com.collectivity.service;

import com.collectivity.controller.dto.CollectivityLocalStatistics;
import com.collectivity.controller.dto.CollectivityOverallStatistics;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final CollectivityRepository collectivityRepository;

    /**
     * Endpoint G : statistiques locales d'une collectivité sur une période.
     * Vérifie l'existence de la collectivité, puis délègue entièrement
     * le calcul au repository (push-down processing).
     */
    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException(
                        "Collectivity.id= " + collectivityId + " not found"));
        return statisticsRepository.getLocalStatistics(collectivityId, from, to);
    }

    /**
     * Endpoint H : statistiques globales de toutes les collectivités sur une période.
     * Délègue entièrement le calcul au repository (push-down processing).
     */
    public List<CollectivityOverallStatistics> getOverallStatistics(
            LocalDate from, LocalDate to) {
        return statisticsRepository.getOverallStatistics(from, to);
    }
}