package com.careerpro.backend.service;

import com.careerpro.backend.entity.*;
import com.careerpro.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSkillRepository skillRepository;
    private final CareerRecommendationRepository recommendationRepository;
    private final ResumeAnalysisRepository resumeRepository;
    private final CareerResultRepository assessmentRepository;
    private final NotificationRepository notificationRepository;
    private final LearningPathRepository learningPathRepository;
    private final UserRepository userRepository;
    private final CareerRecommendationService careerRecommendationService;

    @Transactional
    public Map<String, Object> getDashboardData(Long userId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // Skills stats
        List<UserSkill> skills = skillRepository.findByUserId(userId);
        dashboard.put("totalSkills", skills.size());

        Map<String, Long> skillsByCategory = skills.stream()
                .collect(Collectors.groupingBy(UserSkill::getCategory, Collectors.counting()));
        dashboard.put("skillsByCategory", skillsByCategory);

        double avgProficiency = skills.stream()
                .mapToInt(UserSkill::getProficiency)
                .average().orElse(0.0);
        dashboard.put("averageSkillProficiency", Math.round(avgProficiency * 10.0) / 10.0);

        // Career recommendations
        List<CareerRecommendation> recs = recommendationRepository.findByUserIdOrderByRankOrderAsc(userId);
        
        // Auto-generate if no recommendations are found but user has provided data (e.g. skills)
        if (recs.isEmpty() && !skills.isEmpty()) {
            try {
                recs = careerRecommendationService.generateRecommendations(userId);
            } catch (Exception e) {
                // Silently handle error if AI generation fails during dashboard load
            }
        }
        
        dashboard.put("hasRecommendations", !recs.isEmpty());
        if (!recs.isEmpty()) {
            CareerRecommendation top = recs.get(0);
            Map<String, Object> topCareer = new HashMap<>();
            topCareer.put("title", top.getCareerTitle());
            topCareer.put("confidence", top.getConfidenceScore());
            topCareer.put("salaryRange", top.getSalaryRange());
            topCareer.put("demandLevel", top.getDemandLevel());
            dashboard.put("topCareerMatch", topCareer);
            dashboard.put("totalCareerOptions", recs.size());
        }

        // Resume score
        resumeRepository.findTopByUserIdOrderByAnalyzedAtDesc(userId)
                .ifPresent(r -> dashboard.put("resumeScore", r.getOverallScore()));

        // Assessment result
        assessmentRepository.findTopByUserIdOrderByTestDateDesc(userId)
                .ifPresent(r -> {
                    dashboard.put("dominantInterest", r.getDominantCategory());
                    dashboard.put("assessmentTaken", true);
                });

        // Unread notifications count
        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        dashboard.put("unreadNotifications", unread);

        // Career score (computed)
        int careerScore = calculateCareerScore(skills.size(), !recs.isEmpty(),
                resumeRepository.findTopByUserIdOrderByAnalyzedAtDesc(userId)
                        .map(ResumeAnalysis::getOverallScore).orElse(0),
                assessmentRepository.findTopByUserIdOrderByTestDateDesc(userId).isPresent());
        dashboard.put("careerScore", careerScore);

        // Learning progress
        Optional<LearningPath> lpOpt = learningPathRepository.findTopByUserIdOrderByGeneratedAtDesc(userId);
        
        // Auto-generate if no roadmap is found but user has a top career match
        if (lpOpt.isEmpty() && !recs.isEmpty()) {
            try {
                careerRecommendationService.generateLearningRoadmap(userId, recs.get(0).getCareerTitle());
                lpOpt = learningPathRepository.findTopByUserIdOrderByGeneratedAtDesc(userId);
            } catch (Exception e) {
                // Silently handle error if AI generation fails during dashboard load
            }
        }
        
        lpOpt.ifPresent(lp -> {
            dashboard.put("learningProgress", lp.getCompletionPercent());
            dashboard.put("currentCareerPath", lp.getCareerTitle());
        });

        return dashboard;
    }

    private int calculateCareerScore(int skillCount, boolean hasRecs, int resumeScore, boolean assessmentDone) {
        int score = 0;
        score += Math.min(skillCount * 5, 30);   // Up to 30 points for skills
        score += hasRecs ? 20 : 0;                // 20 points for having recommendations
        score += (int) (resumeScore * 0.3);        // Up to 30 points for resume
        score += assessmentDone ? 20 : 0;          // 20 points for taking assessment
        return Math.min(score, 100);
    }
}
