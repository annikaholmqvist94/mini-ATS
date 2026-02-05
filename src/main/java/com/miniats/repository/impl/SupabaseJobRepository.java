package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.enums.JobStatus;
import com.miniats.domain.model.Job;
import com.miniats.repository.JobRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of JobRepository.
 */
@Repository
public class SupabaseJobRepository extends BaseSupabaseRepository
        implements JobRepository {

    public SupabaseJobRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "jobs";
    }

    @Override
    public Optional<Job> findById(UUID id) {
        try {
            String url = buildTableUrl(eq("id", id));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Job> findByOrganizationId(UUID organizationId) {
        try {
            String url = buildTableUrl(eq("organization_id", organizationId));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Job> findByOrganizationIdAndStatus(UUID organizationId, JobStatus status) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + eq("status", status.name())
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Job> findActiveJobsByOrganizationId(UUID organizationId) {
        return findByOrganizationIdAndStatus(organizationId, JobStatus.ACTIVE);
    }

    @Override
    public List<Job> findByOrganizationIdAndTitleContaining(UUID organizationId, String titleKeyword) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + ilike("title", titleKeyword)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Job> findByOrganizationIdAndDepartment(UUID organizationId, String department) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + eq("department", department)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Job> findAll() {
        try {
            String url = buildTableUrl();
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Job save(Job job) {
        Map<String, Object> data = new HashMap<>();
        data.put("organization_id", job.getOrganizationId().toString());
        data.put("title", job.getTitle());
        data.put("status", job.getStatus().name());

        if (job.getDescription() != null) data.put("description", job.getDescription());
        if (job.getDepartment() != null) data.put("department", job.getDepartment());
        if (job.getLocation() != null) data.put("location", job.getLocation());
        if (job.getCreatedBy() != null) data.put("created_by", job.getCreatedBy().toString());

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Job update(Job job) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", job.getTitle());
        data.put("status", job.getStatus().name());

        if (job.getDescription() != null) data.put("description", job.getDescription());
        if (job.getDepartment() != null) data.put("department", job.getDepartment());
        if (job.getLocation() != null) data.put("location", job.getLocation());

        String url = buildTableUrl(eq("id", job.getId()));
        Map<String, Object> result = executePatch(url, data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public void deleteById(UUID id) {
        String url = buildTableUrl(eq("id", id));
        executeDelete(url);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existsByIdAndOrganizationId(UUID id, UUID organizationId) {
        try {
            String url = buildTableUrl(
                    eq("id", id) + "&" + eq("organization_id", organizationId)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long countByOrganizationId(UUID organizationId) {
        return findByOrganizationId(organizationId).size();
    }

    @Override
    public long countActiveJobsByOrganizationId(UUID organizationId) {
        return findActiveJobsByOrganizationId(organizationId).size();
    }

    private Job mapToEntity(Map<String, Object> row) {
        return Job.builder()
                .id(UUID.fromString((String) row.get("id")))
                .organizationId(UUID.fromString((String) row.get("organization_id")))
                .title((String) row.get("title"))
                .description((String) row.get("description"))
                .department((String) row.get("department"))
                .location((String) row.get("location"))
                .status(JobStatus.fromString((String) row.get("status")))
                .createdBy(row.get("created_by") != null ?
                        UUID.fromString((String) row.get("created_by")) : null)
                .createdAt(parseInstant(row.get("created_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}