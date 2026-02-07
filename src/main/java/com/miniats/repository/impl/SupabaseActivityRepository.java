package com.miniats.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.model.Activity;
import com.miniats.repository.ActivityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Supabase implementation of ActivityRepository.
 */
@Repository
public class SupabaseActivityRepository extends BaseSupabaseRepository implements ActivityRepository {

    private static final String TABLE_NAME = "activities";

    public SupabaseActivityRepository(SupabaseConfig supabaseConfig, ObjectMapper objectMapper) {
        super(supabaseConfig, objectMapper, TABLE_NAME);
    }

    @Override
    public Activity create(Activity activity) {
        String endpoint = "/" + TABLE_NAME;
        return executePost(endpoint, activity, Activity.class);
    }

    @Override
    public List<Activity> findByCandidateId(UUID candidateId) {
        String endpoint = "/" + TABLE_NAME + "?candidate_id=eq." + candidateId + "&order=created_at.desc";
        return executeGetList(endpoint, Activity[].class);
    }

    @Override
    public List<Activity> findByOrganizationId(UUID organizationId) {
        String endpoint = "/" + TABLE_NAME + "?organization_id=eq." + organizationId + "&order=created_at.desc";
        return executeGetList(endpoint, Activity[].class);
    }

    @Override
    public List<Activity> findRecentByCandidateId(UUID candidateId, int limit) {
        String endpoint = "/" + TABLE_NAME +
                "?candidate_id=eq." + candidateId +
                "&order=created_at.desc" +
                "&limit=" + limit;
        return executeGetList(endpoint, Activity[].class);
    }
}