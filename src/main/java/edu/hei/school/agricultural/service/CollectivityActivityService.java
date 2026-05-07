package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityActivityDTO;
import edu.hei.school.agricultural.controller.dto.CreateCollectivityActivityDTO;
import edu.hei.school.agricultural.entity.CollectivityActivity;
import edu.hei.school.agricultural.exception.ResourceNotFoundException;
import edu.hei.school.agricultural.mapper.CollectivityActivityMapper;
import edu.hei.school.agricultural.repository.CollectivityActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectivityActivityService {
    
    private final CollectivityActivityRepository activityRepository;
    private final CollectivityActivityMapper activityMapper;
    
    public List<CollectivityActivityDTO> addActivities(String collectivityId,
                                                       List<CreateCollectivityActivityDTO> activities) {
        for (CreateCollectivityActivityDTO dto : activities) {
            if (dto.getRecurrenceRule() != null && dto.getExecutiveDate() != null) {
                throw new IllegalArgumentException("Cannot provide both recurrence rule and executive date");
            }
        }
        
        List<CollectivityActivity> savedActivities = activities.stream()
            .map(dto -> {
                CollectivityActivity entity = activityMapper.toEntity(dto, collectivityId);
                entity.setId(generateId());
                return entity;
            })
            .map(activityRepository::save)
            .collect(Collectors.toList());
        
        return savedActivities.stream()
            .map(activityMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<CollectivityActivityDTO> getActivities(String collectivityId) {
        return activityRepository.findByCollectivityId(collectivityId).stream()
            .map(activityMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public CollectivityActivity getActivityById(String collectivityId, String activityId) throws ResourceNotFoundException {
        if (!activityRepository.existsByIdAndCollectivityId(activityId, collectivityId)) {
            throw new ResourceNotFoundException("Activity not found for this collectivity");
        }
        return activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
    }
    
    private String generateId() {
        return "act_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}