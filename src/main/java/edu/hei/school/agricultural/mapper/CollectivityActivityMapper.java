package edu.hei.school.agricultural.mapper;

import edu.hei.school.agricultural.controller.dto.CreateCollectivityActivityDTO;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import org.springframework.stereotype.Component;

@Component
public class CollectivityActivityMapper {
    
    public CollectivityActivity toEntity(CreateCollectivityActivityDTO dto, String collectivityId) {
        CollectivityActivity activity = new CollectivityActivity();
        activity.setLabel(dto.getLabel());
        activity.setActivityType(dto.getActivityType());
        activity.setMemberOccupationConcerned(dto.getMemberOccupationConcerned());
        activity.setRecurrenceRule(dto.getRecurrenceRule());
        activity.setExecutiveDate(dto.getExecutiveDate());
        activity.setCollectivityId(collectivityId);
        return activity;
    }
    
    public CollectivityActivityDTO toDTO(CollectivityActivity activity) {
        CollectivityActivityDTO dto = new CollectivityActivityDTO();
        dto.setId(activity.getId());
        dto.setLabel(activity.getLabel());
        dto.setActivityType(activity.getActivityType());
        dto.setMemberOccupationConcerned(activity.getMemberOccupationConcerned());
        dto.setRecurrenceRule(activity.getRecurrenceRule());
        dto.setExecutiveDate(activity.getExecutiveDate());
        return dto;
    }
}