package com.miniats.repository;

import com.miniats.domain.model.Activity;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Activity entity.
 */
public interface ActivityRepository {

    /**
     * Create a new activity
     */
    Activity create(Activity activity);

    /**
     * Find all activities for a candidate
     */
    List<Activity> findByCandidateId(UUID candidateId);

    /**
     * Find all activities for an organization
     */
    List<Activity> findByOrganizationId(UUID organizationId);

    /**
     * Find recent activities for a candidate (limit to latest N)
     */
    List<Activity> findRecentByCandidateId(UUID candidateId, int limit);
}