package com.miniats.controller;

import com.miniats.dto.ApplicationDTO;
import com.miniats.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Application management and Kanban board.
 * Base path: /api/applications
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * GET /api/applications/{id}
     * Get application by ID (with enriched data)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDTO>> getApplicationById(
            @PathVariable UUID id
    ) {
        logger.info("GET /api/applications/{} - Fetching application", id);
        ApplicationDTO application = applicationService.getApplicationById(id);
        return success(application);
    }

    /**
     * GET /api/applications/job/{jobId}
     * Get all applications for a job (Kanban view for single job)
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByJob(
            @PathVariable UUID jobId
    ) {
        logger.info("GET /api/applications/job/{} - Fetching applications", jobId);
        List<ApplicationDTO> applications = applicationService.getApplicationsByJob(jobId);
        return success(applications);
    }

    /**
     * GET /api/applications/job/{jobId}/status/{status}
     * Get applications by job and status (Kanban column)
     */
    @GetMapping("/job/{jobId}/status/{status}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByJobAndStatus(
            @PathVariable UUID jobId,
            @PathVariable String status
    ) {
        logger.info("GET /api/applications/job/{}/status/{} - Fetching applications",
                jobId, status);
        List<ApplicationDTO> applications = applicationService.getApplicationsByJobAndStatus(
                jobId, status);
        return success(applications);
    }

    /**
     * GET /api/applications/candidate/{candidateId}
     * Get all applications for a candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByCandidate(
            @PathVariable UUID candidateId
    ) {
        logger.info("GET /api/applications/candidate/{} - Fetching applications", candidateId);
        List<ApplicationDTO> applications = applicationService.getApplicationsByCandidate(
                candidateId);
        return success(applications);
    }

    /**
     * GET /api/applications/organization/{organizationId}
     * Get all applications for an organization (Full Kanban view)
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/applications/organization/{} - Fetching applications",
                organizationId);
        List<ApplicationDTO> applications = applicationService.getApplicationsByOrganization(
                organizationId);
        return success(applications);
    }

    /**
     * GET /api/applications/organization/{organizationId}/job/{jobId}
     * Get applications by organization and job (Filtered Kanban)
     */
    @GetMapping("/organization/{organizationId}/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByOrganizationAndJob(
            @PathVariable UUID organizationId,
            @PathVariable UUID jobId
    ) {
        logger.info("GET /api/applications/organization/{}/job/{} - Fetching applications",
                organizationId, jobId);
        List<ApplicationDTO> applications = applicationService.getApplicationsByOrganizationAndJob(
                organizationId, jobId);
        return success(applications);
    }

    /**
     * GET /api/applications/organization/{organizationId}/status/{status}
     * Get applications by organization and status (Kanban column across all jobs)
     */
    @GetMapping("/organization/{organizationId}/status/{status}")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsByOrganizationAndStatus(
            @PathVariable UUID organizationId,
            @PathVariable String status
    ) {
        logger.info("GET /api/applications/organization/{}/status/{} - Fetching applications",
                organizationId, status);
        List<ApplicationDTO> applications =
                applicationService.getApplicationsByOrganizationAndStatus(organizationId, status);
        return success(applications);
    }

    /**
     * GET /api/applications/organization/{organizationId}/search
     * Search applications by candidate name within organization
     */
    @GetMapping("/organization/{organizationId}/search")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> searchApplicationsByCandidateName(
            @PathVariable UUID organizationId,
            @RequestParam String candidateName
    ) {
        logger.info("GET /api/applications/organization/{}/search?candidateName={}",
                organizationId, candidateName);
        List<ApplicationDTO> applications =
                applicationService.searchApplicationsByCandidateName(organizationId, candidateName);
        return success(applications);
    }

    /**
     * POST /api/applications
     * Create new application (add candidate to job)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationDTO>> createApplication(
            @RequestBody ApplicationDTO applicationDTO
    ) {
        logger.info("POST /api/applications - Creating application for job: {} and candidate: {}",
                applicationDTO.jobId(), applicationDTO.candidateId());
        ApplicationDTO created = applicationService.createApplication(applicationDTO);
        return created(created);
    }

    /**
     * PATCH /api/applications/{id}/status
     * Update application status (move in Kanban)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request
    ) {
        logger.info("PATCH /api/applications/{}/status - Updating status to: {}",
                id, request.status);
        ApplicationDTO updated = applicationService.updateApplicationStatus(
                id, request.status, request.notes);
        return success(updated);
    }

    /**
     * PATCH /api/applications/{id}/advance
     * Move application to next stage in pipeline
     */
    @PatchMapping("/{id}/advance")
    public ResponseEntity<ApiResponse<ApplicationDTO>> advanceApplication(
            @PathVariable UUID id
    ) {
        logger.info("PATCH /api/applications/{}/advance - Advancing application", id);
        ApplicationDTO advanced = applicationService.advanceApplication(id);
        return success(advanced);
    }

    /**
     * PATCH /api/applications/{id}/reject
     * Reject application
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ApplicationDTO>> rejectApplication(
            @PathVariable UUID id,
            @RequestBody RejectRequest request
    ) {
        logger.info("PATCH /api/applications/{}/reject - Rejecting application", id);
        ApplicationDTO rejected = applicationService.rejectApplication(id, request.reason);
        return success(rejected);
    }

    /**
     * PATCH /api/applications/{id}/offer
     * Move application to OFFER stage
     */
    @PatchMapping("/{id}/offer")
    public ResponseEntity<ApiResponse<ApplicationDTO>> makeOffer(
            @PathVariable UUID id,
            @RequestBody OfferRequest request
    ) {
        logger.info("PATCH /api/applications/{}/offer - Making offer", id);
        ApplicationDTO offered = applicationService.makeOffer(id, request.notes);
        return success(offered);
    }

    /**
     * PATCH /api/applications/{id}/notes
     * Update application notes
     */
    @PatchMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationNotes(
            @PathVariable UUID id,
            @RequestBody UpdateNotesRequest request
    ) {
        logger.info("PATCH /api/applications/{}/notes - Updating notes", id);
        ApplicationDTO updated = applicationService.updateApplicationNotes(id, request.notes);
        return success(updated);
    }

    /**
     * DELETE /api/applications/{id}
     * Delete application
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        logger.info("DELETE /api/applications/{} - Deleting application", id);
        applicationService.deleteApplication(id);
        return noContent();
    }

    /**
     * GET /api/applications/job/{jobId}/count
     * Get application count for job
     */
    @GetMapping("/job/{jobId}/count")
    public ResponseEntity<ApiResponse<Long>> getApplicationCountForJob(
            @PathVariable UUID jobId
    ) {
        logger.info("GET /api/applications/job/{}/count - Getting application count", jobId);
        long count = applicationService.getApplicationCountForJob(jobId);
        return success(count);
    }

    /**
     * GET /api/applications/job/{jobId}/status/{status}/count
     * Get application count by status for job
     */
    @GetMapping("/job/{jobId}/status/{status}/count")
    public ResponseEntity<ApiResponse<Long>> getApplicationCountByStatus(
            @PathVariable UUID jobId,
            @PathVariable String status
    ) {
        logger.info("GET /api/applications/job/{}/status/{}/count - Getting count",
                jobId, status);
        long count = applicationService.getApplicationCountByStatus(jobId, status);
        return success(count);
    }

    /**
     * GET /api/applications/organization/{organizationId}/count
     * Get application count for organization
     */
    @GetMapping("/organization/{organizationId}/count")
    public ResponseEntity<ApiResponse<Long>> getApplicationCountForOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/applications/organization/{}/count - Getting application count",
                organizationId);
        long count = applicationService.getApplicationCountForOrganization(organizationId);
        return success(count);
    }

    /**
     * GET /api/applications/job/{jobId}/stats
     * Get Kanban statistics for a job
     */
    @GetMapping("/job/{jobId}/stats")
    public ResponseEntity<ApiResponse<ApplicationService.KanbanStats>> getKanbanStatsForJob(
            @PathVariable UUID jobId
    ) {
        logger.info("GET /api/applications/job/{}/stats - Getting Kanban statistics", jobId);
        ApplicationService.KanbanStats stats = applicationService.getKanbanStatsForJob(jobId);
        return success(stats);
    }

    /**
     * Request object for updating status
     */
    public record UpdateStatusRequest(String status, String notes) {}

    /**
     * Request object for rejecting application
     */
    public record RejectRequest(String reason) {}

    /**
     * Request object for making offer
     */
    public record OfferRequest(String notes) {}

    /**
     * Request object for updating notes
     */
    public record UpdateNotesRequest(String notes) {}
}