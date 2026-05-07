package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.CollectivityLocalStatistics;
import edu.hei.school.agricultural.controller.dto.CollectivityOverallStatistics;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * GET /collectivites/{id}/statistics
     *
     * Retourne les statistiques locales d'une collectivité sur une période :
     * - montant encaissé par membre
     * - montant impayé potentiel par membre (cotisations ACTIVE uniquement)
     *
     * Note : le path utilise "collectivites" (sans 'y') conformément à la spécification v0.0.5.
     */
    @GetMapping("/collectivites/{id}/statistics")
    public ResponseEntity<?> getCollectivityLocalStatistics(
            @PathVariable String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        try {
            List<CollectivityLocalStatistics> stats =
                    statisticsService.getLocalStatistics(id, from, to);
            return ResponseEntity.ok(stats);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * GET /collectivites/statistics
     *
     * Retourne les statistiques globales de toutes les collectivités sur une période :
     * - % de membres à jour dans leurs cotisations (cotisations ACTIVE seulement)
     * - nombre de nouveaux adhérents
     *
     * Note : le path utilise "collectivites" (sans 'y') conformément à la spécification v0.0.5.
     */
    @GetMapping("/collectivites/statistics")
    public ResponseEntity<?> getCollectivitiesOverallStatistics(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        try {
            List<CollectivityOverallStatistics> stats =
                    statisticsService.getOverallStatistics(from, to);
            return ResponseEntity.ok(stats);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
