package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.dto.SkillDto;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all skills for current user")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getUserSkills(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String category) {
        List<SkillDto> skills;
        if (category != null && !category.isBlank()) {
            skills = skillService.getUserSkillsByCategory(principal.getId(), category);
        } else {
            skills = skillService.getUserSkills(principal.getId());
        }
        return ResponseEntity.ok(ApiResponse.success(skills));
    }

    @PostMapping
    @Operation(summary = "Add a new skill")
    public ResponseEntity<ApiResponse<SkillDto>> addSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SkillDto dto) {
        SkillDto skill = skillService.addSkill(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(skill, "Skill added successfully!"));
    }

    @PutMapping("/{skillId}")
    @Operation(summary = "Update an existing skill")
    public ResponseEntity<ApiResponse<SkillDto>> updateSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long skillId,
            @Valid @RequestBody SkillDto dto) {
        SkillDto skill = skillService.updateSkill(principal.getId(), skillId, dto);
        return ResponseEntity.ok(ApiResponse.success(skill, "Skill updated successfully!"));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long skillId) {
        skillService.deleteSkill(principal.getId(), skillId);
        return ResponseEntity.ok(ApiResponse.success(null, "Skill deleted successfully!"));
    }
}
