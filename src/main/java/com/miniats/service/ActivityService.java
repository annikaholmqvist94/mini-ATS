package com.miniats.service;

import com.miniats.domain.model.Activity;
import com.miniats.dto.ActivityDTO;
import com.miniats.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing activities.
 */
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Create a new activity
     */
    public ActivityDTO createActivity(ActivityDTO activityDTO) {
        Activity activity = Activity.builder()
                .organizationId(activityDTO.organizationId())
                .candidateId(activityDTO.candidateId())
                .applicationId(activityDTO.applicationId())
                .activityType(activityDTO.activityType())
                .description(activityDTO.description())
                .metadata(activityDTO.metadata() != null ? activityDTO.metadata() : Map.of())
                .createdBy(activityDTO.createdBy())
                .createdAt(Instant.now())
                .build();

        Activity created = activityRepository.create(activity);
        return ActivityDTO.fromEntity(created);
    }

    /**
     * Log an activity (helper method)
     */
    public void logActivity(
            UUID organizationId,
            UUID candidateId,
            String activityType,
            String description
    ) {
        Activity activity = Activity.builder()
                .organizationId(organizationId)
                .candidateId(candidateId)
                .activityType(activityType)
                .description(description)
                .createdAt(Instant.now())
                .build();

        activityRepository.create(activity);
    }

    /**
     * Get all activities for a candidate
     */
    public List<ActivityDTO> getActivitiesByCandidate(UUID candidateId) {
        return activityRepository.findByCandidateId(candidateId)
                .stream()
                .map(ActivityDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get recent activities for a candidate (latest 10)
     */
    public List<ActivityDTO> getRecentActivitiesByCandidate(UUID candidateId) {
        return activityRepository.findRecentByCandidateId(candidateId, 10)
                .stream()
                .map(ActivityDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all activities for an organization
     */
    public List<ActivityDTO> getActivitiesByOrganization(UUID organizationId) {
        return activityRepository.findByOrganizationId(organizationId)
                .stream()
                .map(ActivityDTO::fromEntity)
                .collect(Collectors.toList());
    }
}