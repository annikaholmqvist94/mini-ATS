package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.enums.UserRole;
import com.miniats.domain.model.User;
import com.miniats.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of UserRepository.
 */
@Repository
public class SupabaseUserRepository extends BaseSupabaseRepository
        implements UserRepository {

    public SupabaseUserRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            String url = buildTableUrl(eq("id", id));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            String url = buildTableUrl(eq("email", email));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByOrganizationId(UUID organizationId) {
        try {
            String url = buildTableUrl(eq("organization_id", organizationId));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<User> findByRole(UserRole role) {
        try {
            String url = buildTableUrl(eq("role", role.name()));
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<User> findByOrganizationIdAndRole(UUID organizationId, UserRole role) {
        try {
            String url = buildTableUrl(
                    eq("organization_id", organizationId) + "&" + eq("role", role.name())
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<User> findAll() {
        try {
            String url = buildTableUrl();
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.stream().map(this::mapToEntity).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public User save(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("organization_id", user.getOrganizationId().toString());
        data.put("email", user.getEmail());
        data.put("role", user.getRole().name());
        if (user.getFullName() != null) {
            data.put("full_name", user.getFullName());
        }

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public User update(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("role", user.getRole().name());
        if (user.getFullName() != null) {
            data.put("full_name", user.getFullName());
        }

        String url = buildTableUrl(eq("id", user.getId()));
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
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByEmailAndOrganizationId(String email, UUID organizationId) {
        try {
            String url = buildTableUrl(
                    eq("email", email) + "&" + eq("organization_id", organizationId)
            );
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private User mapToEntity(Map<String, Object> row) {
        return User.builder()
                .id(UUID.fromString((String) row.get("id")))
                .organizationId(UUID.fromString((String) row.get("organization_id")))
                .email((String) row.get("email"))
                .role(UserRole.fromString((String) row.get("role")))
                .fullName((String) row.get("full_name"))
                .createdAt(parseInstant(row.get("created_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}