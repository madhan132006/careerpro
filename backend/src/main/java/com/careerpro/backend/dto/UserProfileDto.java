package com.careerpro.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String city;
    private String state;
    private String country;
    private String college;
    private String department;
    private String degree;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private String careerGoal;
    private String aboutMe;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String profilePhotoUrl;
    private String resumeUrl;
    private String languages;
    private Integer experienceYears;
}
