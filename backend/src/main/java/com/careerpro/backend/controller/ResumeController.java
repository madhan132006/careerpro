package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@Tag(name = "Resume Analyzer", description = "AI-powered resume analysis APIs")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/analyze")
    @Operation(summary = "Upload and analyze a PDF resume")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> analysis = resumeService.analyzeResume(principal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success(analysis, "Resume analyzed successfully!"));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the latest resume analysis result")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLatestAnalysis(
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> analysis = resumeService.getLatestAnalysis(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }
}
