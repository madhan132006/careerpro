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
public class AssessmentService {

    private final CareerTestRepository careerTestRepository;
    private final CareerResultRepository careerResultRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQuestions() {
        return careerTestRepository.findByIsActiveTrue().stream()
                .map(q -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", q.getId());
                    map.put("question", q.getQuestion());
                    map.put("category", q.getCategory());
                    map.put("options", List.of(
                            Map.of("label", "A", "text", q.getOptionA(), "weight", q.getWeightA()),
                            Map.of("label", "B", "text", q.getOptionB(), "weight", q.getWeightB()),
                            Map.of("label", "C", "text", q.getOptionC(), "weight", q.getWeightC()),
                            Map.of("label", "D", "text", q.getOptionD(), "weight", q.getWeightD())
                    ));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> submitAssessment(Long userId, List<Map<String, Object>> answers) {
        Map<String, Integer> categoryScores = new HashMap<>();
        categoryScores.put("TECHNICAL", 0);
        categoryScores.put("CREATIVE", 0);
        categoryScores.put("BUSINESS", 0);
        categoryScores.put("RESEARCH", 0);
        categoryScores.put("MANAGEMENT", 0);

        for (Map<String, Object> answer : answers) {
            Long questionId = Long.valueOf(answer.get("questionId").toString());
            String selectedOption = answer.get("selectedOption").toString();

            careerTestRepository.findById(questionId).ifPresent(q -> {
                int weight = switch (selectedOption.toUpperCase()) {
                    case "A" -> q.getWeightA();
                    case "B" -> q.getWeightB();
                    case "C" -> q.getWeightC();
                    case "D" -> q.getWeightD();
                    default -> 1;
                };
                categoryScores.merge(q.getCategory(), weight, Integer::sum);
            });
        }

        // Find dominant category
        String dominant = categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("TECHNICAL");

        int total = categoryScores.values().stream().mapToInt(Integer::intValue).sum();

        User user = userRepository.findById(userId).orElseThrow();

        CareerResult result = CareerResult.builder()
                .user(user)
                .technicalScore(categoryScores.get("TECHNICAL"))
                .creativeScore(categoryScores.get("CREATIVE"))
                .businessScore(categoryScores.get("BUSINESS"))
                .researchScore(categoryScores.get("RESEARCH"))
                .managementScore(categoryScores.get("MANAGEMENT"))
                .totalScore(total)
                .dominantCategory(dominant)
                .build();

        result = careerResultRepository.save(result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", result.getId());
        response.put("scores", categoryScores);
        response.put("totalScore", total);
        response.put("dominantCategory", dominant);
        response.put("dominantLabel", getCategoryLabel(dominant));
        response.put("interpretation", getInterpretation(dominant));
        response.put("testDate", result.getTestDate());
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLatestResult(Long userId) {
        return careerResultRepository.findTopByUserIdOrderByTestDateDesc(userId)
                .map(r -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("technicalScore", r.getTechnicalScore());
                    map.put("creativeScore", r.getCreativeScore());
                    map.put("businessScore", r.getBusinessScore());
                    map.put("researchScore", r.getResearchScore());
                    map.put("managementScore", r.getManagementScore());
                    map.put("totalScore", r.getTotalScore());
                    map.put("dominantCategory", r.getDominantCategory());
                    map.put("testDate", r.getTestDate());
                    return map;
                })
                .orElse(new HashMap<>());
    }

    private String getCategoryLabel(String category) {
        return switch (category) {
            case "TECHNICAL" -> "Tech & Engineering";
            case "CREATIVE" -> "Creative & Design";
            case "BUSINESS" -> "Business & Entrepreneurship";
            case "RESEARCH" -> "Research & Academia";
            case "MANAGEMENT" -> "Management & Leadership";
            default -> category;
        };
    }

    private String getInterpretation(String dominant) {
        return switch (dominant) {
            case "TECHNICAL" -> "You have a strong analytical mindset and love solving technical problems. Careers in Software Engineering, Data Science, and AI are excellent fits.";
            case "CREATIVE" -> "You have a creative and artistic mindset. Careers in UI/UX Design, Content Creation, and Digital Marketing align with your strengths.";
            case "BUSINESS" -> "You have a strong business acumen. Careers in Product Management, Business Analysis, and Entrepreneurship suit you well.";
            case "RESEARCH" -> "You are curious and detail-oriented. Careers in Data Science, Research, and Academic roles match your profile.";
            case "MANAGEMENT" -> "You are a natural leader. Careers in Project Management, Team Lead, and Operations Management are great fits.";
            default -> "Your profile shows a balanced set of interests across multiple domains.";
        };
    }
}
