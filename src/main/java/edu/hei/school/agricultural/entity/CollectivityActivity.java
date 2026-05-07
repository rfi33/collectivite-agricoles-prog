package edu.hei.school.agricultural.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityActivity {
    private String id;
    private String label;
    private ActivityType activityType;
    private List<MemberOccupation> memberOccupationConcerned = new ArrayList<>();
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
    private String collectivityId;
    
    public enum ActivityType {
        MEETING, TRAINING, OTHER
    }
    
    public enum MemberOccupation {
        JUNIOR, SENIOR, SECRETARY, TREASURER, VICE_PRESIDENT, PRESIDENT
    }
}