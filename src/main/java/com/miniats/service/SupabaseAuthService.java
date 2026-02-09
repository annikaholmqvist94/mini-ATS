package com.miniats.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for interacting with Supabase Auth Admin API.
 * Uses Service Role Key to create/delete users.
 */
@Service
public class SupabaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthService.class);

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    private final RestTemplate restTemplate;

    public SupabaseAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Create user in Supabase Auth (admin only operation)
     *
     * @param email User email
     * @param password User password
     * @return Auth User ID (UUID as string)
     */
    public String createAuthUser(String email, String password) {
        try {
            String url = supabaseUrl + "/auth/v1/admin/users";

            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", serviceRoleKey);
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);
            body.put("email_confirm", true);  // Auto-confirm

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            logger.info("🔵 Creating auth user: {}", email);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String authUserId = (String) response.getBody().get("id");
                logger.info("✅ Auth user created: {} (ID: {})", email, authUserId);
                return authUserId;
            } else {
                throw new RuntimeException("Failed to create auth user: " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("❌ Error creating auth user: {}", e.getMessage());
            throw new RuntimeException("Failed to create auth user: " + e.getMessage(), e);
        }
    }

    /**
     * Delete user from Supabase Auth
     */
    public void deleteAuthUser(String authUserId) {
        try {
            String url = supabaseUrl + "/auth/v1/admin/users/" + authUserId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", serviceRoleKey);
            headers.set("Authorization", "Bearer " + serviceRoleKey);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.delete(url);

            logger.info("✅ Auth user deleted: {}", authUserId);

        } catch (Exception e) {
            logger.error("❌ Error deleting auth user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete auth user: " + e.getMessage(), e);
        }
    }
}