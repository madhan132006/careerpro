package com.careerpro.backend.service;

import com.careerpro.backend.dto.SkillDto;
import com.careerpro.backend.entity.User;
import com.careerpro.backend.entity.UserSkill;
import com.careerpro.backend.exception.BadRequestException;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.UserRepository;
import com.careerpro.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(Long userId) {
        return userSkillRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkillsByCategory(Long userId, String category) {
        return userSkillRepository.findByUserIdAndCategory(userId, category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto addSkill(Long userId, SkillDto dto) {
        // Check for duplicate
        if (userSkillRepository.findByUserIdAndSkillName(userId, dto.getSkillName()).isPresent()) {
            throw new BadRequestException("Skill '" + dto.getSkillName() + "' already added. Please edit it instead.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSkill skill = UserSkill.builder()
                .user(user)
                .skillName(dto.getSkillName().trim())
                .category(dto.getCategory())
                .proficiency(dto.getProficiency())
                .build();

        skill = userSkillRepository.save(skill);
        return mapToDto(skill);
    }

    @Transactional
    public SkillDto updateSkill(Long userId, Long skillId, SkillDto dto) {
        UserSkill skill = userSkillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (!skill.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only edit your own skills");
        }

        skill.setSkillName(dto.getSkillName().trim());
        skill.setCategory(dto.getCategory());
        skill.setProficiency(dto.getProficiency());

        skill = userSkillRepository.save(skill);
        return mapToDto(skill);
    }

    @Transactional
    public void deleteSkill(Long userId, Long skillId) {
        UserSkill skill = userSkillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (!skill.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own skills");
        }

        userSkillRepository.delete(skill);
    }

    private SkillDto mapToDto(UserSkill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .skillName(skill.getSkillName())
                .category(skill.getCategory())
                .proficiency(skill.getProficiency())
                .build();
    }
}
