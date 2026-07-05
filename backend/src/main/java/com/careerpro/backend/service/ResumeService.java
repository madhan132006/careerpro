package com.careerpro.backend.service;

import com.careerpro.backend.ai.GeminiAiClient;
import com.careerpro.backend.ai.PromptTemplateEngine;
import com.careerpro.backend.entity.*;
import com.careerpro.backend.exception.BadRequestException;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final GeminiAiClient geminiClient;
    private final PromptTemplateEngine promptEngine;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Map<String, Object> analyzeResume(Long userId, MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("Please upload a resume file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BadRequestException("Only PDF files are supported");
        }

        // Save the resume file
        String filename = "resume_" + userId + "_" + UUID.randomUUID() + ".pdf";
        Path uploadPath = Paths.get("uploads/resumes");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Extract text from PDF
        String extractedText = extractTextFromPdf(file);
        log.debug("Extracted {} characters from resume", extractedText.length());

        if (extractedText.trim().isEmpty()) {
            throw new BadRequestException("Could not extract text from the PDF. Please ensure it is not scanned/image-based.");
        }

        // Build prompt and analyze with Gemini
        String prompt = promptEngine.buildResumeAnalysisPrompt(extractedText);
        String aiResponse = geminiClient.generateContent(prompt);

        // Parse the analysis
        JsonNode json = geminiClient.parseResponse(aiResponse);

        // Save to database
        User user = userRepository.findById(userId).orElseThrow();

        JsonNode scores = json.path("scores");
        int atsScore = scores.path("atsScore").asInt(60);
        int formattingScore = scores.path("formattingScore").asInt(70);
        int grammarScore = scores.path("grammarScore").asInt(80);
        int overallScore = scores.path("overallScore").asInt(70);

        JsonNode extracted = json.path("extractedInfo");

        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .extractedText(extractedText.substring(0, Math.min(extractedText.length(), 5000)))
                .skillsFound(arrayNodeToString(extracted.path("skills")))
                .projectsFound(arrayNodeToString(extracted.path("projects")))
                .achievementsFound(arrayNodeToString(extracted.path("achievements")))
                .atsScore(atsScore)
                .formattingScore(formattingScore)
                .grammarScore(grammarScore)
                .overallScore(overallScore)
                .improvementTips(aiResponse)
                .build();

        resumeAnalysisRepository.save(analysis);

        // Update profile with resume URL
        userProfileRepository.findByUserId(userId).ifPresent(p -> {
            p.setResumeUrl("/api/files/uploads/resumes/" + filename);
            userProfileRepository.save(p);
        });

        // Create notification
        Notification notification = Notification.builder()
                .user(user)
                .title("📄 Resume Analysis Complete")
                .message("Your resume scored " + overallScore + "/100. Check improvement tips!")
                .type("INFO")
                .build();
        notificationRepository.save(notification);

        return geminiClient.getObjectMapper().convertValue(json, Map.class);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLatestAnalysis(Long userId) {
        return resumeAnalysisRepository.findTopByUserIdOrderByAnalyzedAtDesc(userId)
                .map(analysis -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("fileName", analysis.getFileName());
                    result.put("atsScore", analysis.getAtsScore());
                    result.put("formattingScore", analysis.getFormattingScore());
                    result.put("grammarScore", analysis.getGrammarScore());
                    result.put("overallScore", analysis.getOverallScore());
                    result.put("skillsFound", analysis.getSkillsFound());
                    result.put("projectsFound", analysis.getProjectsFound());
                    result.put("achievementsFound", analysis.getAchievementsFound());
                    result.put("improvementTips", analysis.getImprovementTips());
                    result.put("analyzedAt", analysis.getAnalyzedAt());
                    return result;
                })
                .orElse(new HashMap<>());
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String arrayNodeToString(JsonNode node) {
        if (node == null || node.isNull() || !node.isArray()) return "";
        List<String> items = new ArrayList<>();
        node.forEach(n -> items.add(n.asText()));
        return String.join(", ", items);
    }
}
