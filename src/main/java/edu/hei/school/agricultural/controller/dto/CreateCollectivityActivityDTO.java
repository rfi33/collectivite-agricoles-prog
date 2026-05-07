// dto/CreateCollectivityActivityDTO.java
package edu.hei.school.agricultural.controller.dto;

import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.entity.MonthlyRecurrenceRule;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCollectivityActivityDTO {
    private String label;
    private CollectivityActivity.ActivityType activityType;
    private List<CollectivityActivity.MemberOccupation> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
}