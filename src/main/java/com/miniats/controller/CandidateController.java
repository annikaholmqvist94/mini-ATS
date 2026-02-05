package com.miniats.controller;

import com.miniats.dto.CandidateDTO;
import com.miniats.service.CandidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Candidate management.
 * Base path: /candidates
 */
@RestController
@RequestMapping("/candidates")
public class CandidateController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /**
     * GET /api/candidates
     * Get all candidates (admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateDTO>>> getAllCandidates() {
        logger.info("GET /api/candidates - Fetching all candidates");
        List<CandidateDTO> candidates = candidateService.getAllCandidates();
        return success(candidates);
    }

    /**
     * GET /api/candidates/{id}
     * Get candidate by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateDTO>> getCandidateById(
            @PathVariable UUID id
    ) {
        logger.info("GET /api/candidates/{} - Fetching candidate", id);
        CandidateDTO candidate = candidateService.getCandidateById(id);
        return success(candidate);
    }

    /**
     * GET /api/candidates/organization/{organizationId}
     * Get all candidates for an organization
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<CandidateDTO>>> getCandidatesByOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/candidates/organization/{} - Fetching candidates", organizationId);
        List<CandidateDTO> candidates = candidateService.getCandidatesByOrganization(organizationId);
        return success(candidates);
    }

    /**
     * GET /api/candidates/organization/{organizationId}/search
     * Search candidates by name within organization
     */
    @GetMapping("/organization/{organizationId}/search")
    public ResponseEntity<ApiResponse<List<CandidateDTO>>> searchCandidatesByName(
            @PathVariable UUID organizationId,
            @RequestParam String name
    ) {
        logger.info("GET /api/candidates/organization/{}/search?name={} - Searching candidates",
                organizationId, name);
        List<CandidateDTO> candidates = candidateService.searchCandidatesByName(
                organizationId, name);
        return success(candidates);
    }

    /**
     * GET /api/candidates/organization/{organizationId}/email/{email}
     * Get candidate by email within organization
     */
    @GetMapping("/organization/{organizationId}/email/{email}")
    public ResponseEntity<ApiResponse<CandidateDTO>> getCandidateByEmail(
            @PathVariable UUID organizationId,
            @PathVariable String email
    ) {
        logger.info("GET /api/candidates/organization/{}/email/{} - Fetching candidate",
                organizationId, email);
        CandidateDTO candidate = candidateService.getCandidateByEmail(organizationId, email);
        return success(candidate);
    }

    /**
     * GET /api/candidates/organization/{organizationId}/linkedin
     * Search candidates by LinkedIn URL within organization
     */
    @GetMapping("/organization/{organizationId}/linkedin")
    public ResponseEntity<ApiResponse<List<CandidateDTO>>> searchCandidatesByLinkedIn(
            @PathVariable UUID organizationId,
            @RequestParam String keyword
    ) {
        logger.info("GET /api/candidates/organization/{}/linkedin?keyword={} - Searching candidates",
                organizationId, keyword);
        List<CandidateDTO> candidates = candidateService.searchCandidatesByLinkedIn(
                organizationId, keyword);
        return success(candidates);
    }

    /**
     * POST /api/candidates
     * Create new candidate
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CandidateDTO>> createCandidate(
            @RequestBody CandidateDTO candidateDTO
    ) {
        logger.info("POST /api/candidates - Creating candidate: {}", candidateDTO.fullName());
        CandidateDTO created = candidateService.createCandidate(candidateDTO);
        return created(created);
    }

    /**
     * PUT /api/candidates/{id}
     * Update candidate
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateDTO>> updateCandidate(
            @PathVariable UUID id,
            @RequestBody CandidateDTO candidateDTO
    ) {
        logger.info("PUT /api/candidates/{} - Updating candidate", id);
        CandidateDTO updated = candidateService.updateCandidate(id, candidateDTO);
        return success(updated);
    }

    /**
     * DELETE /api/candidates/{id}
     * Delete candidate
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        logger.info("DELETE /api/candidates/{} - Deleting candidate", id);
        candidateService.deleteCandidate(id);
        return noContent();
    }

    /**
     * GET /api/candidates/organization/{organizationId}/count
     * Get candidate count for organization
     */
    @GetMapping("/organization/{organizationId}/count")
    public ResponseEntity<ApiResponse<Long>> getCandidateCount(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/candidates/organization/{}/count - Getting candidate count",
                organizationId);
        long count = candidateService.getCandidateCount(organizationId);
        return success(count);
    }
}