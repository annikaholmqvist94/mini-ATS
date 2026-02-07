package com.miniats.controller;

import com.miniats.dto.ActivityDTO;
import com.miniats.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Activity endpoints.
 */
@RestController
@RequestMapping("/activities")
public class ActivityController extends BaseController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Get recent activities for a candidate
     * GET /api/activities/candidate/{candidateId}
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getActivitiesByCandidate(
            @PathVariable UUID candidateId
    ) {
        List<ActivityDTO> activities = activityService.getRecentActivitiesByCandidate(candidateId);
        return success(activities);
    }

    /**
     * Get all activities for an organization
     * GET /api/activities/organization/{organizationId}
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getActivitiesByOrganization(
            @PathVariable UUID organizationId
    ) {
        List<ActivityDTO> activities = activityService.getActivitiesByOrganization(organizationId);
        return success(activities);
    }

    /**
     * Create a new activity
     * POST /api/activities
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(
            @RequestBody ActivityDTO activityDTO
    ) {
        ActivityDTO created = activityService.createActivity(activityDTO);
        return success(created);
    }
}