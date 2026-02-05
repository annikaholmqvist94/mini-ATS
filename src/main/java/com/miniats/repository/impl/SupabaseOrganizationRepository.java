package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.model.Organization;
import com.miniats.repository.OrganizationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of OrganizationRepository.
 * Uses Supabase REST API for data access.
 */
@Repository
public class SupabaseOrganizationRepository extends BaseSupabaseRepository
        implements OrganizationRepository {

    public SupabaseOrganizationRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "organizations";
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        try {
            String url = buildTableUrl(eq("id", id));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});

            if (results.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Organization> findByName(String name) {
        try {
            String url = buildTableUrl(eq("name", name));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});

            if (results.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Organization> findAll() {
        try {
            String url = buildTableUrl();
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Organization save(Organization organization) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", organization.getName());

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Organization update(Organization organization) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", organization.getName());

        String url = buildTableUrl(eq("id", organization.getId()));
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
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }

    /**
     * Map Supabase row to Organization entity
     */
    private Organization mapToEntity(Map<String, Object> row) {
        return Organization.builder()
                .id(UUID.fromString((String) row.get("id")))
                .name((String) row.get("name"))
                .createdAt(parseInstant(row.get("created_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .build();
    }

    /**
     * Parse timestamp string to Instant
     */
    private Instant parseInstant(Object value) {
        if (value == null) {
            return null;
        }
        return Instant.parse((String) value);
    }
}