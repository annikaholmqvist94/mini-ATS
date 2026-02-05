package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Base class for Supabase repository implementations.
 * Provides common REST API operations using Supabase REST API.
 */
public abstract class BaseSupabaseRepository {

    protected final RestTemplate restTemplate;
    protected final SupabaseConfig supabaseConfig;
    protected final ObjectMapper objectMapper;

    protected BaseSupabaseRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.supabaseConfig = supabaseConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * Get the table name for this repository
     */
    protected abstract String getTableName();

    /**
     * Build URL for table operations
     */
    protected String buildTableUrl() {
        return supabaseConfig.getRestApiUrl() + "/" + getTableName();
    }

    /**
     * Build URL with query parameters
     */
    protected String buildTableUrl(String queryParams) {
        return buildTableUrl() + "?" + queryParams;
    }

    /**
     * Execute GET request and parse response
     */
    protected <T> List<T> executeGet(String url, TypeReference<List<T>> typeRef) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(supabaseConfig.getServiceRoleHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isEmpty()) {
                return List.of();
            }

            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute GET request: " + e.getMessage(), e);
        }
    }

    /**
     * Execute POST request (insert)
     */
    protected <T> T executePost(String url, Object body, Class<T> responseType) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, supabaseConfig.getServiceRoleHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("Empty response from POST request");
            }

            // Supabase returns array, get first element
            List<T> results = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, responseType)
            );

            if (results.isEmpty()) {
                throw new RuntimeException("No data returned from POST request");
            }

            return results.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }

    /**
     * Execute PATCH request (update)
     */
    protected <T> T executePatch(String url, Object body, Class<T> responseType) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, supabaseConfig.getServiceRoleHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("Empty response from PATCH request");
            }

            // Supabase returns array, get first element
            List<T> results = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, responseType)
            );

            if (results.isEmpty()) {
                throw new RuntimeException("No data returned from PATCH request");
            }

            return results.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute PATCH request: " + e.getMessage(), e);
        }
    }

    /**
     * Execute DELETE request
     */
    protected void executeDelete(String url) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(supabaseConfig.getServiceRoleHeaders());
            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute DELETE request: " + e.getMessage(), e);
        }
    }

    /**
     * Build filter query parameter
     */
    protected String buildFilter(String column, String operator, Object value) {
        return column + "=" + operator + "." + value;
    }

    /**
     * Build equals filter
     */
    protected String eq(String column, Object value) {
        return buildFilter(column, "eq", value);
    }

    /**
     * Build LIKE filter (case-insensitive search)
     */
    protected String ilike(String column, String pattern) {
        return buildFilter(column, "ilike", "*" + pattern + "*");
    }

    /**
     * Convert map to JSON string for POST/PATCH
     */
    protected String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize data to JSON", e);
        }
    }
}