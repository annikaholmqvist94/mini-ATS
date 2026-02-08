package com.miniats.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniats.config.SupabaseConfig;
import com.miniats.domain.model.Scorecard;
import com.miniats.repository.ScorecardRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Supabase implementation of ScorecardRepository.
 */
@Repository
public class SupabaseScorecardRepository extends BaseSupabaseRepository implements ScorecardRepository {

    public SupabaseScorecardRepository(
            RestTemplate restTemplate,
            SupabaseConfig supabaseConfig,
            ObjectMapper objectMapper
    ) {
        super(restTemplate, supabaseConfig, objectMapper);
    }

    @Override
    protected String getTableName() {
        return "scorecards";
    }

    @Override
    public Scorecard create(Scorecard scorecard) {
        Map<String, Object> data = new HashMap<>();
        data.put("candidate_id", scorecard.getCandidateId().toString());
        data.put("organization_id", scorecard.getOrganizationId().toString());

        if (scorecard.getTechnicalSkills() != null) data.put("technical_skills", scorecard.getTechnicalSkills());
        if (scorecard.getCommunication() != null) data.put("communication", scorecard.getCommunication());
        if (scorecard.getCulturalFit() != null) data.put("cultural_fit", scorecard.getCulturalFit());
        if (scorecard.getExperienceLevel() != null) data.put("experience_level", scorecard.getExperienceLevel());
        if (scorecard.getProblemSolving() != null) data.put("problem_solving", scorecard.getProblemSolving());
        if (scorecard.getOverallScore() != null) data.put("overall_score", scorecard.getOverallScore());
        if (scorecard.getEvaluatedBy() != null) data.put("evaluated_by", scorecard.getEvaluatedBy().toString());
        if (scorecard.getNotes() != null) data.put("notes", scorecard.getNotes());

        Map<String, Object> result = executePost(buildTableUrl(), data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Scorecard update(Scorecard scorecard) {
        Map<String, Object> data = new HashMap<>();

        if (scorecard.getTechnicalSkills() != null) data.put("technical_skills", scorecard.getTechnicalSkills());
        if (scorecard.getCommunication() != null) data.put("communication", scorecard.getCommunication());
        if (scorecard.getCulturalFit() != null) data.put("cultural_fit", scorecard.getCulturalFit());
        if (scorecard.getExperienceLevel() != null) data.put("experience_level", scorecard.getExperienceLevel());
        if (scorecard.getProblemSolving() != null) data.put("problem_solving", scorecard.getProblemSolving());
        if (scorecard.getOverallScore() != null) data.put("overall_score", scorecard.getOverallScore());
        //if (scorecard.getEvaluatedBy() != null) data.put("evaluated_by", scorecard.getEvaluatedBy().toString());
        if (scorecard.getNotes() != null) data.put("notes", scorecard.getNotes());

        String url = buildTableUrl(eq("candidate_id", scorecard.getCandidateId()));
        Map<String, Object> result = executePatch(url, data, Map.class);
        return mapToEntity(result);
    }

    @Override
    public Optional<Scorecard> findByCandidateId(UUID candidateId) {
        try {
            String url = buildTableUrl(eq("candidate_id", candidateId) + "&limit=1");
            List<Map<String, Object>> results = executeGet(url, new TypeReference<>() {});
            return results.isEmpty() ? Optional.empty() : Optional.of(mapToEntity(results.get(0)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByCandidateId(UUID candidateId) {
        String url = buildTableUrl(eq("candidate_id", candidateId));
        executeDelete(url);
    }

    @Override
    public boolean existsByCandidateId(UUID candidateId) {
        return findByCandidateId(candidateId).isPresent();
    }

    private Scorecard mapToEntity(Map<String, Object> row) {
        return Scorecard.builder()
                .id(UUID.fromString((String) row.get("id")))
                .candidateId(UUID.fromString((String) row.get("candidate_id")))
                .organizationId(UUID.fromString((String) row.get("organization_id")))
                .technicalSkills((Integer) row.get("technical_skills"))
                .communication((Integer) row.get("communication"))
                .culturalFit((Integer) row.get("cultural_fit"))
                .experienceLevel((Integer) row.get("experience_level"))
                .problemSolving((Integer) row.get("problem_solving"))
                //.evaluatedBy(row.get("evaluated_by") != null ? UUID.fromString((String) row.get("evaluated_by")) : null)
                .notes((String) row.get("notes"))
                .createdAt(parseInstant(row.get("created_at")))
                .updatedAt(parseInstant(row.get("updated_at")))
                .build();
    }

    private Instant parseInstant(Object value) {
        return value == null ? null : Instant.parse((String) value);
    }
}