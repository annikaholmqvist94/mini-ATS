package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.model.Candidate;
import com.miniats.repository.CandidateRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of CandidateRepository.
 */
@Repository
public class SupabaseCandidateRepository extends BaseSupabaseRepository
        implements CandidateRepository {

    public SupabaseCandidateRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "candidates";
    }

    @Override
    public Optional<Candidate> findById(UUID id) {
        try {
            String url = buildTableUrl(eq("id", id));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Candidate> findByOrganizationId(UUID organizationId) {
        try {
            String url = buildTableUrl(eq("organization_id", organizationId));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Candidate> findByOrganizationIdAndFullNameContaining(
            UUID organizationId,
            String nameKeyword
    ) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + ilike("full_name", nameKeyword)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Optional<Candidate> findByOrganizationIdAndEmail(UUID organizationId, String email) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + eq("email", email)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Candidate> findByOrganizationIdAndLinkedinUrlContaining(
            UUID organizationId,
            String linkedinKeyword
    ) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + ilike("linkedin_url", linkedinKeyword)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Candidate> findAll() {
        try {
            String url = buildTableUrl();
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Candidate save(Candidate candidate) {
        Map<String, Object> data = new HashMap<>();
        data.put("organization_id", candidate.getOrganizationId().toString());
        data.put("full_name", candidate.getFullName());

        if (candidate.getEmail() != null) data.put("email", candidate.getEmail());
        if (candidate.getPhone() != null) data.put("phone", candidate.getPhone());
        if (candidate.getLinkedinUrl() != null) data.put("linkedin_url", candidate.getLinkedinUrl());
        if (candidate.getResumeUrl() != null) data.put("resume_url", candidate.getResumeUrl());
        if (candidate.getNotes() != null) data.put("notes", candidate.getNotes());

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Candidate update(Candidate candidate) {
        Map<String, Object> data = new HashMap<>();
        data.put("full_name", candidate.getFullName());

        if (candidate.getEmail() != null) data.put("email", candidate.getEmail());
        if (candidate.getPhone() != null) data.put("phone", candidate.getPhone());
        if (candidate.getLinkedinUrl() != null) data.put("linkedin_url", candidate.getLinkedinUrl());
        if (candidate.getResumeUrl() != null) data.put("resume_url", candidate.getResumeUrl());
        if (candidate.getNotes() != null) data.put("notes", candidate.getNotes());

        String url = buildTableUrl(eq("id", candidate.getId()));
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
    public boolean existsByOrganizationIdAndEmail(UUID organizationId, String email) {
        return findByOrganizationIdAndEmail(organizationId, email).isPresent();
    }

    @Override
    public long countByOrganizationId(UUID organizationId) {
        return findByOrganizationId(organizationId).size();
    }

    private Candidate mapToEntity(Map<String, Object> row) {
        return Candidate.builder()
                .id(UUID.fromString((String) row.get("id")))
                .organizationId(UUID.fromString((String) row.get("organization_id")))
                .fullName((String) row.get("full_name"))
                .email((String) row.get("email"))
                .phone((String) row.get("phone"))
                .linkedinUrl((String) row.get("linkedin_url"))
                .resumeUrl((String) row.get("resume_url"))
                .notes((String) row.get("notes"))
                .createdAt(parseInstant(row.get("created_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}