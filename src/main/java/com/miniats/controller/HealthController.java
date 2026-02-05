package com.miniats.controller;

import com.miniats.config.SupabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status.
 * Base path: /api/health
 */
@RestController
@RequestMapping("/health")
public class HealthController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final SupabaseConfig supabaseConfig;

    public HealthController(SupabaseConfig supabaseConfig) {
        this.supabaseConfig = supabaseConfig;
    }

    /**
     * GET /api/health
     * Basic health check
     */
    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        logger.debug("GET /api/health - Health check");
        return success("OK");
    }

    /**
     * GET /api/health/status
     * Detailed status information
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
        logger.debug("GET /api/health/status - Detailed status check");

        Map<String, Object> status = new HashMap<>();
        status.put("application", "Mini-ATS");
        status.put("version", "1.0.0");
        status.put("status", "UP");
        status.put("timestamp", Instant.now());
        status.put("supabase", Map.of(
                "configured", supabaseConfig.getSupabaseUrl() != null,
                "url", maskUrl(supabaseConfig.getSupabaseUrl())
        ));

        return success(status);
    }

    /**
     * GET /api/health/ready
     * Readiness probe (for Kubernetes/Docker)
     */
    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<String>> ready() {
        logger.debug("GET /api/health/ready - Readiness check");
        // Add checks for database connectivity, etc.
        return success("READY");
    }

    /**
     * GET /api/health/live
     * Liveness probe (for Kubernetes/Docker)
     */
    @GetMapping("/live")
    public ResponseEntity<ApiResponse<String>> live() {
        logger.debug("GET /api/health/live - Liveness check");
        return success("ALIVE");
    }

    /**
     * Mask sensitive URL information
     */
    private String maskUrl(String url) {
        if (url == null) {
            return "not-configured";
        }
        // Mask the URL to hide project ID
        return url.replaceAll("([a-z0-9]{20})", "***");
    }
}