package com.careerpro.backend.service;

import com.careerpro.backend.ai.GeminiAiClient;
import com.careerpro.backend.ai.PromptTemplateEngine;
import com.careerpro.backend.entity.InterviewQuestion;
import com.careerpro.backend.entity.User;
import com.careerpro.backend.repository.InterviewQuestionRepository;
import com.careerpro.backend.repository.UserRepository;
import com.careerpro.backend.repository.UserSkillRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final GeminiAiClient geminiClient;
    private final PromptTemplateEngine promptEngine;
    private final InterviewQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    @Transactional
    public List<Map<String, Object>> generateInterviewQuestions(Long userId, String careerTitle, String questionType) {
        var skills = userSkillRepository.findByUserId(userId);

        String prompt = promptEngine.buildInterviewPrepPrompt(careerTitle, skills, questionType);
        String aiResponse = geminiClient.generateContent(prompt);

        JsonNode root = geminiClient.parseResponse(aiResponse);
        JsonNode questions = root.path("questions");

        User user = userRepository.findById(userId).orElseThrow();

        // Delete old questions of same type
        questionRepository.deleteByUserId(userId);

        List<Map<String, Object>> result = new ArrayList<>();

        if (questions.isArray()) {
            for (JsonNode q : questions) {
                InterviewQuestion iq = InterviewQuestion.builder()
                        .user(user)
                        .careerTitle(careerTitle)
                        .questionType(questionType)
                        .question(q.path("question").asText())
                        .modelAnswer(q.path("modelAnswer").asText())
                        .difficulty(q.path("difficulty").asText("MEDIUM"))
                        .build();
                questionRepository.save(iq);

                Map<String, Object> qMap = new LinkedHashMap<>();
                qMap.put("id", iq.getId());
                qMap.put("question", iq.getQuestion());
                qMap.put("modelAnswer", iq.getModelAnswer());
                qMap.put("difficulty", iq.getDifficulty());
                qMap.put("questionType", iq.getQuestionType());
                qMap.put("tips", q.path("tips").asText(""));
                result.add(qMap);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInterviewQuestions(Long userId, String questionType) {
        List<InterviewQuestion> questions;
        if (questionType != null && !questionType.isBlank()) {
            questions = questionRepository.findByUserIdAndQuestionType(userId, questionType);
        } else {
            questions = questionRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        }

        return questions.stream().map(q -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", q.getId());
            map.put("question", q.getQuestion());
            map.put("modelAnswer", q.getModelAnswer());
            map.put("difficulty", q.getDifficulty());
            map.put("questionType", q.getQuestionType());
            map.put("careerTitle", q.getCareerTitle());
            return map;
        }).collect(Collectors.toList());
    }
}
