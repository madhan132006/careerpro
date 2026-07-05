package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.AssessmentService;
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
@RequestMapping("/assessment")
@RequiredArgsConstructor
@Tag(name = "Assessment", description = "Career interest assessment APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/questions")
    @Operation(summary = "Get all assessment questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions() {
        return ResponseEntity.ok(ApiResponse.success(assessmentService.getQuestions()));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit assessment answers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitAssessment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody List<Map<String, Object>> answers) {
        Map<String, Object> result = assessmentService.submitAssessment(principal.getId(), answers);
        return ResponseEntity.ok(ApiResponse.success(result, "Assessment completed! Check your results."));
    }

    @GetMapping("/result")
    @Operation(summary = "Get latest assessment result")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getResult(
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> result = assessmentService.getLatestResult(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
