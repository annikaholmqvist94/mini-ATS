package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.model.Activity;
import com.miniats.repository.ActivityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of ActivityRepository.
 */
@Repository
public class SupabaseActivityRepository extends BaseSupabaseRepository implements ActivityRepository {

    public SupabaseActivityRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "activities";
    }

    @Override
    public Activity create(Activity activity) {
        Map<String, Object> data = new HashMap<>();
        data.put("organization_id", activity.getOrganizationId().toString());

        if (activity.getCandidateId() != null) data.put("candidate_id", activity.getCandidateId().toString());
        if (activity.getApplicationId() != null) data.put("application_id", activity.getApplicationId().toString());
        data.put("activity_type", activity.getActivityType());
        data.put("description", activity.getDescription());
        if (activity.getMetadata() != null && !activity.getMetadata().isEmpty()) {
            data.put("metadata", activity.getMetadata());
        }
        if (activity.getCreatedBy() != null) data.put("created_by", activity.getCreatedBy().toString());

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public List<Activity> findByCandidateId(UUID candidateId) {
        try {
            String url = buildTableUrl(eq("candidate_id", candidateId) + "&order=created_at.desc");
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Activity> findByOrganizationId(UUID organizationId) {
        try {
            String url = buildTableUrl(eq("organization_id", organizationId) + "&order=created_at.desc");
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Activity> findRecentByCandidateId(UUID candidateId, int limit) {
        try {
            String url = buildTableUrl(
                    eq("candidate_id", candidateId) +
                            "&order=created_at.desc" +
                            "&limit=" + limit
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private Activity mapToEntity(Map<String, Object> row) {
        return Activity.builder()
                .id(UUID.fromString((String) row.get("id")))
                .organizationId(UUID.fromString((String) row.get("organization_id")))
                .candidateId(row.get("candidate_id") != null ? UUID.fromString((String) row.get("candidate_id")) : null)
                .applicationId(row.get("application_id") != null ? UUID.fromString((String) row.get("application_id")) : null)
                .activityType((String) row.get("activity_type"))
                .description((String) row.get("description"))
                .metadata(row.get("metadata") != null ? (Map<String, Object>) row.get("metadata") : Map.of())
                .createdBy(row.get("created_by") != null ? UUID.fromString((String) row.get("created_by")) : null)
                .createdAt(parseInstant(row.get("created_at")))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}