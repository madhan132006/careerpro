package com.careerpro.backend.service;

import com.careerpro.backend.dto.UserProfileDto;
import com.careerpro.backend.entity.User;
import com.careerpro.backend.entity.UserProfile;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.UserProfileRepository;
import com.careerpro.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        return mapToDto(user, profile);
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update user's basic info
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userRepository.save(user);

        // Update or create profile
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().user(user).build());

        if (dto.getDob() != null) profile.setDob(dto.getDob());
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getState() != null) profile.setState(dto.getState());
        if (dto.getCountry() != null) profile.setCountry(dto.getCountry());
        if (dto.getCollege() != null) profile.setCollege(dto.getCollege());
        if (dto.getDepartment() != null) profile.setDepartment(dto.getDepartment());
        if (dto.getDegree() != null) profile.setDegree(dto.getDegree());
        if (dto.getGraduationYear() != null) profile.setGraduationYear(dto.getGraduationYear());
        if (dto.getCgpa() != null) profile.setCgpa(dto.getCgpa());
        if (dto.getCareerGoal() != null) profile.setCareerGoal(dto.getCareerGoal());
        if (dto.getAboutMe() != null) profile.setAboutMe(dto.getAboutMe());
        if (dto.getLinkedinUrl() != null) profile.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getGithubUrl() != null) profile.setGithubUrl(dto.getGithubUrl());
        if (dto.getPortfolioUrl() != null) profile.setPortfolioUrl(dto.getPortfolioUrl());
        if (dto.getLanguages() != null) profile.setLanguages(dto.getLanguages());
        if (dto.getExperienceYears() != null) profile.setExperienceYears(dto.getExperienceYears());

        userProfileRepository.save(profile);
        return mapToDto(user, profile);
    }

    @Transactional
    public String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        validateImageFile(file);
        String filename = "photo_" + userId + "_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        String relativePath = saveFile(file, "uploads/photos", filename);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setProfilePhotoUrl("/api/files/" + relativePath);
        userProfileRepository.save(profile);

        return "/api/files/" + relativePath;
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private String saveFile(MultipartFile file, String directory, String filename) throws IOException {
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return directory + "/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    private UserProfileDto mapToDto(User user, UserProfile profile) {
        return UserProfileDto.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(profile.getDob())
                .gender(profile.getGender())
                .city(profile.getCity())
                .state(profile.getState())
                .country(profile.getCountry())
                .college(profile.getCollege())
                .department(profile.getDepartment())
                .degree(profile.getDegree())
                .graduationYear(profile.getGraduationYear())
                .cgpa(profile.getCgpa())
                .careerGoal(profile.getCareerGoal())
                .aboutMe(profile.getAboutMe())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .resumeUrl(profile.getResumeUrl())
                .languages(profile.getLanguages())
                .experienceYears(profile.getExperienceYears())
                .build();
    }
}
