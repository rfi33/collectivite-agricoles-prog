package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;

    @GetMapping("/collectivity/{collectivityId}")
    public ResponseEntity<List<Map<String, Object>>> getMemberStats(
            @PathVariable String collectivityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<Map<String, Object>> stats = statisticsService
            .getMemberStatsByCollectivityAndPeriod(collectivityId, from, to);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/overall")
    public ResponseEntity<List<Map<String, Object>>> getOverallStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<Map<String, Object>> stats = statisticsService
            .getOverallStatisticsForAllCollectivities(from, to);
        
        return ResponseEntity.ok(stats);
    }
}