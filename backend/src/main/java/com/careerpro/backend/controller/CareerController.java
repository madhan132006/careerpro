package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.entity.CareerRecommendation;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.CareerRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/career")
@RequiredArgsConstructor
@Tag(name = "Career AI", description = "AI-powered career recommendation and analysis APIs")
@SecurityRequirement(name = "bearerAuth")
public class CareerController {

    private final CareerRecommendationService careerService;

    @PostMapping("/recommend")
    @Operation(summary = "Generate AI career recommendations")
    public ResponseEntity<ApiResponse<List<CareerRecommendation>>> generateRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<CareerRecommendation> recs = careerService.generateRecommendations(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(recs, "Career recommendations generated successfully!"));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get saved career recommendations")
    public ResponseEntity<ApiResponse<List<CareerRecommendation>>> getRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<CareerRecommendation> recs = careerService.getRecommendations(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(recs));
    }

    @PostMapping("/skill-gap")
    @Operation(summary = "Perform AI skill gap analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> skillGapAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        Map<String, Object> analysis = careerService.generateSkillGapAnalysis(
                principal.getId(), body.get("targetCareer"));
        return ResponseEntity.ok(ApiResponse.success(analysis, "Skill gap analysis complete!"));
    }

    @PostMapping("/roadmap")
    @Operation(summary = "Generate personalized learning roadmap")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateRoadmap(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        Map<String, Object> roadmap = careerService.generateLearningRoadmap(
                principal.getId(), body.get("careerTitle"));
        return ResponseEntity.ok(ApiResponse.success(roadmap, "Learning roadmap generated successfully!"));
    }
}
