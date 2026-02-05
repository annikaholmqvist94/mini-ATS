package com.miniats.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Supabase configuration for connecting to PostgreSQL database.
 * Provides HTTP client and RestTemplate beans for Supabase REST API calls.
 */
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

    /**
     * Get Supabase base URL
     */
    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    /**
     * Get Supabase anonymous key (for client-side auth)
     */
    public String getSupabaseAnonKey() {
        return supabaseAnonKey;
    }

    /**
     * Get Supabase service role key (bypasses RLS, use carefully!)
     */
    public String getSupabaseServiceRoleKey() {
        return supabaseServiceRoleKey;
    }

    /**
     * Get REST API endpoint URL
     */
    public String getRestApiUrl() {
        return supabaseUrl + "/rest/v1";
    }

    /**
     * Configure HTTP client with connection pooling
     */
    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    /**
     * Configure RestTemplate for Supabase API calls
     */
    @Bean
    public RestTemplate supabaseRestTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(5000);
        requestFactory.setConnectionRequestTimeout(5000);

        return new RestTemplate(requestFactory);
    }

    /**
     * Get common headers for Supabase requests (using service role key)
     */
    public org.springframework.http.HttpHeaders getServiceRoleHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("apikey", supabaseServiceRoleKey);
        headers.set("Authorization", "Bearer " + supabaseServiceRoleKey);
        headers.set("Content-Type", "application/json");
        headers.set("Prefer", "return=representation");
        return headers;
    }

    /**
     * Get common headers for Supabase requests (using anon key with user JWT)
     */
    public org.springframework.http.HttpHeaders getAuthHeaders(String userJwt) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("apikey", supabaseAnonKey);
        headers.set("Authorization", "Bearer " + userJwt);
        headers.set("Content-Type", "application/json");
        headers.set("Prefer", "return=representation");
        return headers;
    }

    /**
     * Configure ObjectMapper for JSON processing
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}