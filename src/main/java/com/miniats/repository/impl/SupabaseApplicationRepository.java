package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.enums.ApplicationStatus;
import com.miniats.domain.model.Application;
import com.miniats.repository.ApplicationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of ApplicationRepository.
 * Critical for Kanban board functionality with job and candidate relationships.
 */
@Repository
public class SupabaseApplicationRepository extends BaseSupabaseRepository
        implements ApplicationRepository {

    public SupabaseApplicationRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "applications";
    }

    @Override
    public Optional<Application> findById(UUID id) {
        try {
            String url = buildTableUrl(eq("id", id));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Application> findByJobIdAndCandidateId(UUID jobId, UUID candidateId) {
        try {
            String url = buildTableUrl(
                    eq("job_id", jobId) + "&" + eq("candidate_id", candidateId)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Application> findByJobId(UUID jobId) {
        try {
            String url = buildTableUrl(eq("job_id", jobId));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByJobIdAndStatus(UUID jobId, ApplicationStatus status) {
        try {
            String url = buildTableUrl(
                    eq("job_id", jobId) + "&" + eq("status", status.name())
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByCandidateId(UUID candidateId) {
        try {
            String url = buildTableUrl(eq("candidate_id", candidateId));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByOrganizationId(UUID organizationId) {
        // This requires a JOIN with jobs table to filter by organization_id
        // Using Supabase's resource embedding syntax
        try {
            String url = supabaseConfig.getRestApiUrl() + "/applications?" +
                    "select=*,jobs!inner(organization_id)&" +
                    "jobs.organization_id=eq." + organizationId;

            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByOrganizationIdAndJobId(UUID organizationId, UUID jobId) {
        // Verify job belongs to organization, then get applications
        try {
            String url = supabaseConfig.getRestApiUrl() + "/applications?" +
                    "select=*,jobs!inner(organization_id)&" +
                    "job_id=eq." + jobId + "&" +
                    "jobs.organization_id=eq." + organizationId;

            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByOrganizationIdAndStatus(
            UUID organizationId,
            ApplicationStatus status
    ) {
        try {
            String url = supabaseConfig.getRestApiUrl() + "/applications?" +
                    "select=*,jobs!inner(organization_id)&" +
                    "status=eq." + status.name() + "&" +
                    "jobs.organization_id=eq." + organizationId;

            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findByOrganizationIdAndCandidateNameContaining(
            UUID organizationId,
            String candidateNameKeyword
    ) {
        // This requires JOIN with both jobs and candidates tables
        try {
            String url = supabaseConfig.getRestApiUrl() + "/applications?" +
                    "select=*,jobs!inner(organization_id),candidates!inner(full_name)&" +
                    "jobs.organization_id=eq." + organizationId + "&" +
                    "candidates.full_name=ilike.*" + candidateNameKeyword + "*";

            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Application> findAll() {
        try {
            String url = buildTableUrl();
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Application save(Application application) {
        Map<String, Object> data = new HashMap<>();
        data.put("job_id", application.getJobId().toString());
        data.put("candidate_id", application.getCandidateId().toString());
        data.put("status", application.getStatus().name());
        data.put("stage_order", application.getStageOrder());

        if (application.getNotes() != null) {
            data.put("notes", application.getNotes());
        }

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Application update(Application application) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", application.getStatus().name());
        data.put("stage_order", application.getStageOrder());

        if (application.getNotes() != null) {
            data.put("notes", application.getNotes());
        }

        String url = buildTableUrl(eq("id", application.getId()));
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
    public boolean existsByJobIdAndCandidateId(UUID jobId, UUID candidateId) {
        return findByJobIdAndCandidateId(jobId, candidateId).isPresent();
    }

    @Override
    public long countByJobId(UUID jobId) {
        return findByJobId(jobId).size();
    }

    @Override
    public long countByJobIdAndStatus(UUID jobId, ApplicationStatus status) {
        return findByJobIdAndStatus(jobId, status).size();
    }

    @Override
    public long countByOrganizationId(UUID organizationId) {
        return findByOrganizationId(organizationId).size();
    }

    private Application mapToEntity(Map<String, Object> row) {
        return Application.builder()
                .id(UUID.fromString((String) row.get("id")))
                .jobId(UUID.fromString((String) row.get("job_id")))
                .candidateId(UUID.fromString((String) row.get("candidate_id")))
                .status(ApplicationStatus.fromString((String) row.get("status")))
                .stageOrder(((Number) row.get("stage_order")).intValue())
                .appliedAt(parseInstant(row.get("applied_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .notes((String) row.get("notes"))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}