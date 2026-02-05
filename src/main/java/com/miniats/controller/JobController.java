package com.miniats.controller;

import com.miniats.dto.JobDTO;
import com.miniats.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Job management.
 * Base path: /jobs
 */
@RestController
@RequestMapping("/jobs")
public class JobController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * GET /api/jobs
     * Get all jobs (admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDTO>>> getAllJobs() {
        logger.info("GET /api/jobs - Fetching all jobs");
        List<JobDTO> jobs = jobService.getAllJobs();
        return success(jobs);
    }

    /**
     * GET /api/jobs/{id}
     * Get job by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDTO>> getJobById(@PathVariable UUID id) {
        logger.info("GET /api/jobs/{} - Fetching job", id);
        JobDTO job = jobService.getJobById(id);
        return success(job);
    }

    /**
     * GET /api/jobs/organization/{organizationId}
     * Get all jobs for an organization
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobsByOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/jobs/organization/{} - Fetching jobs", organizationId);
        List<JobDTO> jobs = jobService.getJobsByOrganization(organizationId);
        return success(jobs);
    }

    /**
     * GET /api/jobs/organization/{organizationId}/active
     * Get active jobs for an organization
     */
    @GetMapping("/organization/{organizationId}/active")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getActiveJobsByOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/jobs/organization/{}/active - Fetching active jobs", organizationId);
        List<JobDTO> jobs = jobService.getActiveJobsByOrganization(organizationId);
        return success(jobs);
    }

    /**
     * GET /api/jobs/organization/{organizationId}/status/{status}
     * Get jobs by organization and status
     */
    @GetMapping("/organization/{organizationId}/status/{status}")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobsByOrganizationAndStatus(
            @PathVariable UUID organizationId,
            @PathVariable String status
    ) {
        logger.info("GET /api/jobs/organization/{}/status/{} - Fetching jobs",
                organizationId, status);
        List<JobDTO> jobs = jobService.getJobsByOrganizationAndStatus(organizationId, status);
        return success(jobs);
    }

    /**
     * GET /api/jobs/organization/{organizationId}/search
     * Search jobs by title within organization
     */
    @GetMapping("/organization/{organizationId}/search")
    public ResponseEntity<ApiResponse<List<JobDTO>>> searchJobsByTitle(
            @PathVariable UUID organizationId,
            @RequestParam String title
    ) {
        logger.info("GET /api/jobs/organization/{}/search?title={} - Searching jobs",
                organizationId, title);
        List<JobDTO> jobs = jobService.searchJobsByTitle(organizationId, title);
        return success(jobs);
    }

    /**
     * GET /api/jobs/organization/{organizationId}/department/{department}
     * Get jobs by department within organization
     */
    @GetMapping("/organization/{organizationId}/department/{department}")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobsByDepartment(
            @PathVariable UUID organizationId,
            @PathVariable String department
    ) {
        logger.info("GET /api/jobs/organization/{}/department/{} - Fetching jobs",
                organizationId, department);
        List<JobDTO> jobs = jobService.getJobsByDepartment(organizationId, department);
        return success(jobs);
    }

    /**
     * POST /api/jobs
     * Create new job
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobDTO>> createJob(@RequestBody JobDTO jobDTO) {
        logger.info("POST /api/jobs - Creating job: {}", jobDTO.title());
        JobDTO created = jobService.createJob(jobDTO);
        return created(created);
    }

    /**
     * PUT /api/jobs/{id}
     * Update job
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(
            @PathVariable UUID id,
            @RequestBody JobDTO jobDTO
    ) {
        logger.info("PUT /api/jobs/{} - Updating job", id);
        JobDTO updated = jobService.updateJob(id, jobDTO);
        return success(updated);
    }

    /**
     * PATCH /api/jobs/{id}/status
     * Update job status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobDTO>> updateJobStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request
    ) {
        logger.info("PATCH /api/jobs/{}/status - Updating status to: {}", id, request.status);
        JobDTO updated = jobService.updateJobStatus(id, request.status);
        return success(updated);
    }

    /**
     * PATCH /api/jobs/{id}/close
     * Close job
     */
    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<JobDTO>> closeJob(@PathVariable UUID id) {
        logger.info("PATCH /api/jobs/{}/close - Closing job", id);
        JobDTO closed = jobService.closeJob(id);
        return success(closed);
    }

    /**
     * PATCH /api/jobs/{id}/activate
     * Activate job
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<JobDTO>> activateJob(@PathVariable UUID id) {
        logger.info("PATCH /api/jobs/{}/activate - Activating job", id);
        JobDTO activated = jobService.activateJob(id);
        return success(activated);
    }

    /**
     * DELETE /api/jobs/{id}
     * Delete job
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        logger.info("DELETE /api/jobs/{} - Deleting job", id);
        jobService.deleteJob(id);
        return noContent();
    }

    /**
     * GET /api/jobs/organization/{organizationId}/count
     * Get job count for organization
     */
    @GetMapping("/organization/{organizationId}/count")
    public ResponseEntity<ApiResponse<Long>> getJobCount(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/jobs/organization/{}/count - Getting job count", organizationId);
        long count = jobService.getJobCount(organizationId);
        return success(count);
    }

    /**
     * GET /api/jobs/organization/{organizationId}/active/count
     * Get active job count for organization
     */
    @GetMapping("/organization/{organizationId}/active/count")
    public ResponseEntity<ApiResponse<Long>> getActiveJobCount(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/jobs/organization/{}/active/count - Getting active job count",
                organizationId);
        long count = jobService.getActiveJobCount(organizationId);
        return success(count);
    }

    /**
     * Request object for updating status
     */
    public record UpdateStatusRequest(String status) {}
}