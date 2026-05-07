package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.CollectivityActivity;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class CollectivityActivityRepository {
    
    private final Map<String, CollectivityActivity> activities = new ConcurrentHashMap<>();
    
    public CollectivityActivity save(CollectivityActivity activity) {
        activities.put(activity.getId(), activity);
        return activity;
    }
    
    public Optional<CollectivityActivity> findById(String id) {
        return Optional.ofNullable(activities.get(id));
    }
    
    public List<CollectivityActivity> findByCollectivityId(String collectivityId) {
        return activities.values().stream()
            .filter(activity -> activity.getCollectivityId().equals(collectivityId))
            .collect(Collectors.toList());
    }
    
    public boolean existsByIdAndCollectivityId(String activityId, String collectivityId) {
        CollectivityActivity activity = activities.get(activityId);
        return activity != null && activity.getCollectivityId().equals(collectivityId);
    }
    
    public void delete(String id) {
        activities.remove(id);
    }
    
    public List<CollectivityActivity> findAll() {
        return new ArrayList<>(activities.values());
    }
}