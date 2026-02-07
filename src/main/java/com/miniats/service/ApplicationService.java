package com.miniats.service;

import com.miniats.domain.enums.ApplicationStatus;
import com.miniats.domain.model.Application;
import com.miniats.domain.model.Candidate;
import com.miniats.domain.model.Job;
import com.miniats.dto.ApplicationDTO;
import com.miniats.dto.CandidateDTO;
import com.miniats.dto.JobDTO;
import com.miniats.repository.ApplicationRepository;
import com.miniats.repository.CandidateRepository;
import com.miniats.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for Application entity operations.
 * Handles business logic for Kanban board and application tracking.
 */
@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final UserService userService;
    private final ActivityService activityService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            CandidateRepository candidateRepository,
            UserService userService,
            ActivityService activityService
    ) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.userService = userService;
        this.activityService = activityService;
    }

    /**
     * Get application by ID (with enriched candidate and job data)
     */
    public ApplicationDTO getApplicationById(UUID id) {
        logger.debug("Fetching application with ID: {}", id);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        return enrichApplicationDTO(application);
    }

    /**
     * Get all applications for a job (Kanban view for single job)
     */
    public List<ApplicationDTO> getApplicationsByJob(UUID jobId) {
        logger.debug("Fetching applications for job: {}", jobId);

        return applicationRepository.findByJobId(jobId).stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Get applications by job and status (Kanban column)
     */
    public List<ApplicationDTO> getApplicationsByJobAndStatus(UUID jobId, String status) {
        logger.debug("Fetching applications for job: {} with status: {}", jobId, status);

        ApplicationStatus appStatus = ApplicationStatus.fromString(status);
        return applicationRepository.findByJobIdAndStatus(jobId, appStatus).stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Get all applications for a candidate
     */
    public List<ApplicationDTO> getApplicationsByCandidate(UUID candidateId) {
        logger.debug("Fetching applications for candidate: {}", candidateId);

        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Get all applications for an organization (Full Kanban view)
     */
    public List<ApplicationDTO> getApplicationsByOrganization(UUID organizationId) {
        logger.debug("Fetching applications for organization: {}", organizationId);

        return applicationRepository.findByOrganizationId(organizationId).stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Get applications by organization and job (Filtered Kanban)
     */
    public List<ApplicationDTO> getApplicationsByOrganizationAndJob(
            UUID organizationId,
            UUID jobId
    ) {
        logger.debug("Fetching applications for organization: {} and job: {}",
                organizationId, jobId);

        return applicationRepository
                .findByOrganizationIdAndJobId(organizationId, jobId)
                .stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Get applications by organization and status (Kanban column across all jobs)
     */
    public List<ApplicationDTO> getApplicationsByOrganizationAndStatus(
            UUID organizationId,
            String status
    ) {
        logger.debug("Fetching applications for organization: {} with status: {}",
                organizationId, status);

        ApplicationStatus appStatus = ApplicationStatus.fromString(status);
        return applicationRepository
                .findByOrganizationIdAndStatus(organizationId, appStatus)
                .stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Search applications by candidate name within organization
     */
    public List<ApplicationDTO> searchApplicationsByCandidateName(
            UUID organizationId,
            String candidateNameKeyword
    ) {
        logger.debug("Searching applications in organization: {} by candidate name: {}",
                organizationId, candidateNameKeyword);

        return applicationRepository
                .findByOrganizationIdAndCandidateNameContaining(organizationId, candidateNameKeyword)
                .stream()
                .map(this::enrichApplicationDTO)
                .toList();
    }

    /**
     * Create new application (add candidate to job)
     */
    public ApplicationDTO createApplication(ApplicationDTO applicationDTO) {
        logger.info("Creating new application for job: {} and candidate: {}",
                applicationDTO.jobId(), applicationDTO.candidateId());

        // Validate job exists
        Job job = jobRepository.findById(applicationDTO.jobId())
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + applicationDTO.jobId()));

        // Validate candidate exists
        Candidate candidate = candidateRepository.findById(applicationDTO.candidateId())
                .orElseThrow(() -> new RuntimeException(
                        "Candidate not found with ID: " + applicationDTO.candidateId()));

        // Verify candidate belongs to same organization as job
        if (!candidate.getOrganizationId().equals(job.getOrganizationId())) {
            throw new RuntimeException("Candidate and job must belong to same organization");
        }

        // Check for duplicate application
        if (applicationRepository.existsByJobIdAndCandidateId(
                applicationDTO.jobId(),
                applicationDTO.candidateId()
        )) {
            throw new RuntimeException("Application already exists for this job and candidate");
        }

        // Create entity
        Application application = Application.builder()
                .jobId(applicationDTO.jobId())
                .candidateId(applicationDTO.candidateId())
                .status(ApplicationStatus.fromString(applicationDTO.status()))
                .notes(applicationDTO.notes())
                .build();

        // Save and return
        Application saved = applicationRepository.save(application);
        logger.info("Application created with ID: {}", saved.getId());

        // Log activity for new application
        activityService.logActivity(
                job.getOrganizationId(),
                applicationDTO.candidateId(),
                "candidate_added",
                "Added to pipeline"
        );
        logger.debug("Activity logged for new application");

        return enrichApplicationDTO(saved);
    }

    /**
     * Update application status (move in Kanban)
     */
    public ApplicationDTO updateApplicationStatus(UUID id, String newStatus, String notes) {
        logger.info("Updating application status for ID: {} to: {}", id, newStatus);

        // Check if application exists
        Application existing = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        ApplicationStatus status = ApplicationStatus.fromString(newStatus);
        String oldStatus = existing.getStatus().name();

        // Validate transition (optional business rule)
        validateStatusTransition(existing.getStatus(), status);

        // Update entity
        Application updated = existing.toBuilder()
                .status(status)
                .notes(notes != null ? notes : existing.getNotes())
                .build();

        // Save and return
        Application saved = applicationRepository.update(updated);
        logger.info("Application status updated: {} -> {}", id, newStatus);

        // Log activity for status change
        if (!oldStatus.equals(newStatus)) {
            // Fetch job to get organizationId
            Job job = jobRepository.findById(existing.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found with ID: " + existing.getJobId()));

            activityService.logActivity(
                    job.getOrganizationId(),
                    existing.getCandidateId(),
                    "status_changed",
                    "Moved from " + oldStatus + " to " + newStatus
            );
            logger.debug("Activity logged for status change: {} -> {}", oldStatus, newStatus);
        }

        return enrichApplicationDTO(saved);
    }

    /**
     * Move application to next stage in pipeline
     */
    public ApplicationDTO advanceApplication(UUID id) {
        Application existing = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        ApplicationStatus currentStatus = existing.getStatus();
        ApplicationStatus nextStatus = getNextStatus(currentStatus);

        if (nextStatus == null) {
            throw new RuntimeException("Cannot advance application beyond " + currentStatus);
        }

        return updateApplicationStatus(id, nextStatus.name(), null);
    }

    /**
     * Reject application
     */
    public ApplicationDTO rejectApplication(UUID id, String reason) {
        return updateApplicationStatus(id, ApplicationStatus.REJECTED.name(), reason);
    }

    /**
     * Move application to OFFER stage
     */
    public ApplicationDTO makeOffer(UUID id, String notes) {
        return updateApplicationStatus(id, ApplicationStatus.OFFER.name(), notes);
    }

    /**
     * Update application notes
     */
    public ApplicationDTO updateApplicationNotes(UUID id, String notes) {
        logger.info("Updating notes for application: {}", id);

        Application existing = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        Application updated = existing.toBuilder()
                .notes(notes)
                .build();

        Application saved = applicationRepository.update(updated);
        return enrichApplicationDTO(saved);
    }

    /**
     * Delete application
     */
    public void deleteApplication(UUID id) {
        logger.info("Deleting application with ID: {}", id);

        if (!applicationRepository.existsById(id)) {
            throw new RuntimeException("Application not found with ID: " + id);
        }

        applicationRepository.deleteById(id);
        logger.info("Application deleted: {}", id);
    }

    /**
     * Get application count for job
     */
    public long getApplicationCountForJob(UUID jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    /**
     * Get application count by status for job
     */
    public long getApplicationCountByStatus(UUID jobId, String status) {
        ApplicationStatus appStatus = ApplicationStatus.fromString(status);
        return applicationRepository.countByJobIdAndStatus(jobId, appStatus);
    }

    /**
     * Get application count for organization
     */
    public long getApplicationCountForOrganization(UUID organizationId) {
        return applicationRepository.countByOrganizationId(organizationId);
    }

    /**
     * Get Kanban statistics for a job
     */
    public KanbanStats getKanbanStatsForJob(UUID jobId) {
        return new KanbanStats(
                applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.NEW),
                applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.SCREENING),
                applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.INTERVIEW),
                applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.OFFER),
                applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.REJECTED)
        );
    }

    /**
     * Verify user has access to application (through job's organization)
     */
    public boolean userHasAccessToApplication(String userEmail, UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return userService.hasAccessToOrganization(userEmail, job.getOrganizationId());
    }

    /**
     * Enrich application DTO with candidate and job data
     */
    private ApplicationDTO enrichApplicationDTO(Application application) {
        CandidateDTO candidate = candidateRepository.findById(application.getCandidateId())
                .map(CandidateDTO::fromEntity)
                .orElse(null);

        JobDTO job = jobRepository.findById(application.getJobId())
                .map(JobDTO::fromEntity)
                .orElse(null);

        return ApplicationDTO.fromEntityEnriched(application, candidate, job);
    }

    /**
     * Validate status transition (business rule)
     */
    private void validateStatusTransition(ApplicationStatus from, ApplicationStatus to) {
        // Can always reject
        if (to == ApplicationStatus.REJECTED) {
            return;
        }

        // Cannot move from REJECTED or OFFER
        if (from == ApplicationStatus.REJECTED || from == ApplicationStatus.OFFER) {
            throw new RuntimeException("Cannot change status from " + from);
        }

        logger.debug("Status transition validated: {} -> {}", from, to);
    }

    /**
     * Get next status in pipeline
     */
    private ApplicationStatus getNextStatus(ApplicationStatus current) {
        return switch (current) {
            case NEW -> ApplicationStatus.SCREENING;
            case SCREENING -> ApplicationStatus.INTERVIEW;
            case INTERVIEW -> ApplicationStatus.OFFER;
            default -> null;
        };
    }

    /**
     * Kanban statistics record
     */
    public record KanbanStats(
            long newCount,
            long screeningCount,
            long interviewCount,
            long offerCount,
            long rejectedCount
    ) {
        public long getTotalCount() {
            return newCount + screeningCount + interviewCount + offerCount + rejectedCount;
        }
    }
}