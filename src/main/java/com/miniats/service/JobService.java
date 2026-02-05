package com.miniats.service;

import com.miniats.domain.enums.JobStatus;
import com.miniats.domain.model.Job;
import com.miniats.dto.JobDTO;
import com.miniats.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for Job entity operations.
 * Handles business logic for job posting management.
 */
@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final OrganizationService organizationService;
    private final UserService userService;

    public JobService(
            JobRepository jobRepository,
            OrganizationService organizationService,
            UserService userService
    ) {
        this.jobRepository = jobRepository;
        this.organizationService = organizationService;
        this.userService = userService;
    }

    /**
     * Get job by ID
     */
    public JobDTO getJobById(UUID id) {
        logger.debug("Fetching job with ID: {}", id);

        return jobRepository.findById(id)
                .map(JobDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
    }

    /**
     * Get all jobs for an organization
     */
    public List<JobDTO> getJobsByOrganization(UUID organizationId) {
        logger.debug("Fetching jobs for organization: {}", organizationId);

        return jobRepository.findByOrganizationId(organizationId).stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Get active jobs for an organization
     */
    public List<JobDTO> getActiveJobsByOrganization(UUID organizationId) {
        logger.debug("Fetching active jobs for organization: {}", organizationId);

        return jobRepository.findActiveJobsByOrganizationId(organizationId).stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Get jobs by status for an organization
     */
    public List<JobDTO> getJobsByOrganizationAndStatus(UUID organizationId, String status) {
        logger.debug("Fetching jobs for organization: {} with status: {}", organizationId, status);

        JobStatus jobStatus = JobStatus.fromString(status);
        return jobRepository.findByOrganizationIdAndStatus(organizationId, jobStatus).stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Search jobs by title within organization
     */
    public List<JobDTO> searchJobsByTitle(UUID organizationId, String titleKeyword) {
        logger.debug("Searching jobs in organization: {} with keyword: {}", organizationId, titleKeyword);

        return jobRepository.findByOrganizationIdAndTitleContaining(organizationId, titleKeyword).stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Get jobs by department within organization
     */
    public List<JobDTO> getJobsByDepartment(UUID organizationId, String department) {
        logger.debug("Fetching jobs for organization: {} in department: {}", organizationId, department);

        return jobRepository.findByOrganizationIdAndDepartment(organizationId, department).stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Get all jobs (admin only)
     */
    public List<JobDTO> getAllJobs() {
        logger.debug("Fetching all jobs");

        return jobRepository.findAll().stream()
                .map(JobDTO::fromEntity)
                .toList();
    }

    /**
     * Create new job
     */
    public JobDTO createJob(JobDTO jobDTO) {
        logger.info("Creating new job: {} for organization: {}",
                jobDTO.title(), jobDTO.organizationId());

        // Validate organization exists
        if (!organizationService.organizationExists(jobDTO.organizationId())) {
            throw new RuntimeException("Organization not found with ID: " + jobDTO.organizationId());
        }

        // Validate creator exists if provided
        if (jobDTO.createdBy() != null) {
            userService.getUserById(jobDTO.createdBy());
        }

        // Create entity
        Job job = Job.builder()
                .organizationId(jobDTO.organizationId())
                .title(jobDTO.title())
                .description(jobDTO.description())
                .department(jobDTO.department())
                .location(jobDTO.location())
                .status(JobStatus.fromString(jobDTO.status()))
                .createdBy(jobDTO.createdBy())
                .build();

        // Save and return
        Job saved = jobRepository.save(job);
        logger.info("Job created with ID: {}", saved.getId());

        return JobDTO.fromEntity(saved);
    }

    /**
     * Update job
     */
    public JobDTO updateJob(UUID id, JobDTO jobDTO) {
        logger.info("Updating job with ID: {}", id);

        // Check if job exists
        Job existing = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));

        // Verify organization match (cannot move job to different organization)
        if (!existing.getOrganizationId().equals(jobDTO.organizationId())) {
            throw new RuntimeException("Cannot change job organization");
        }

        // Update entity
        Job updated = existing.toBuilder()
                .title(jobDTO.title())
                .description(jobDTO.description())
                .department(jobDTO.department())
                .location(jobDTO.location())
                .status(JobStatus.fromString(jobDTO.status()))
                .build();

        // Save and return
        Job saved = jobRepository.update(updated);
        logger.info("Job updated: {}", saved.getId());

        return JobDTO.fromEntity(saved);
    }

    /**
     * Update job status
     */
    public JobDTO updateJobStatus(UUID id, String status) {
        logger.info("Updating job status for ID: {} to: {}", id, status);

        Job existing = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));

        Job updated = existing.toBuilder()
                .status(JobStatus.fromString(status))
                .build();

        Job saved = jobRepository.update(updated);
        logger.info("Job status updated: {}", saved.getId());

        return JobDTO.fromEntity(saved);
    }

    /**
     * Close job (set status to CLOSED)
     */
    public JobDTO closeJob(UUID id) {
        return updateJobStatus(id, JobStatus.CLOSED.name());
    }

    /**
     * Activate job (set status to ACTIVE)
     */
    public JobDTO activateJob(UUID id) {
        return updateJobStatus(id, JobStatus.ACTIVE.name());
    }

    /**
     * Delete job
     */
    public void deleteJob(UUID id) {
        logger.info("Deleting job with ID: {}", id);

        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found with ID: " + id);
        }

        jobRepository.deleteById(id);
        logger.info("Job deleted: {}", id);
    }

    /**
     * Get job count for organization
     */
    public long getJobCount(UUID organizationId) {
        return jobRepository.countByOrganizationId(organizationId);
    }

    /**
     * Get active job count for organization
     */
    public long getActiveJobCount(UUID organizationId) {
        return jobRepository.countActiveJobsByOrganizationId(organizationId);
    }

    /**
     * Verify user has access to job (through organization)
     */
    public boolean userHasAccessToJob(String userEmail, UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return userService.hasAccessToOrganization(userEmail, job.getOrganizationId());
    }
}