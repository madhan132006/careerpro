package com.careerpro.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name too long")
    private String skillName;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 1, message = "Proficiency must be at least 1")
    @Max(value = 5, message = "Proficiency cannot exceed 5")
    private Integer proficiency;
}
