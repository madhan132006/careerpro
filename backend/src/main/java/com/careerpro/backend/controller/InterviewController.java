package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.InterviewService;
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
@RequestMapping("/interview")
@RequiredArgsConstructor
@Tag(name = "Interview Prep", description = "AI-powered interview preparation APIs")
@SecurityRequirement(name = "bearerAuth")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/generate")
    @Operation(summary = "Generate interview questions for a career role")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> generateQuestions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        String careerTitle = body.getOrDefault("careerTitle", "Software Engineer");
        String questionType = body.getOrDefault("questionType", "TECHNICAL");
        List<Map<String, Object>> questions =
                interviewService.generateInterviewQuestions(principal.getId(), careerTitle, questionType);
        return ResponseEntity.ok(ApiResponse.success(questions, "Interview questions generated!"));
    }

    @GetMapping("/questions")
    @Operation(summary = "Get saved interview questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String type) {
        List<Map<String, Object>> questions = interviewService.getInterviewQuestions(principal.getId(), type);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
}
