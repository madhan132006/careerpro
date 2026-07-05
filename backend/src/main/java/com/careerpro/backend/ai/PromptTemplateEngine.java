package com.careerpro.backend.ai;

import com.careerpro.backend.dto.UserProfileDto;
import com.careerpro.backend.entity.UserSkill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PromptTemplateEngine - Central Prompt Engineering Module
 * =========================================================
 * Implements:
 *  - Role Prompting
 *  - Context Injection
 *  - Structured Output Prompting
 *  - Few-Shot Prompting
 *  - Separated System / Developer / User / Output Format layers
 */
@Slf4j
@Component
public class PromptTemplateEngine {

    // ========================================================
    // SYSTEM PROMPT - Role Prompting
    // ========================================================
    private static final String SYSTEM_PROMPT = """
            You are CareerPro AI, an elite career guidance counselor and industry expert with 20+ years of experience
            in talent development, career coaching, and workforce analytics. You specialize in:
            - Analyzing student academic and professional profiles
            - Recommending personalized career paths based on skills, interests, and market trends
            - Providing actionable skill gap analysis and learning roadmaps
            - Resume analysis and optimization for ATS compatibility
            - Interview preparation across technical, HR, and behavioral domains
            
            You always respond with structured, accurate, and actionable JSON-formatted outputs.
            You base recommendations on real-world industry demand and current job market trends.
            Never make up or hallucinate information. Be specific, detailed, and practical.
            """;

    // ========================================================
    // CAREER RECOMMENDATION PROMPT
    // ========================================================
    public String buildCareerRecommendationPrompt(UserProfileDto profile, List<UserSkill> skills,
                                                   String dominantInterest) {
        String skillsList = skills.stream()
                .map(s -> s.getSkillName() + " (Level " + s.getProficiency() + "/5, Category: " + s.getCategory() + ")")
                .collect(Collectors.joining(", "));

        return """
                %s
                
                ## TASK: Career Recommendation Analysis
                
                Analyze the following student profile comprehensively and provide TOP 5 career recommendations.
                
                ## STUDENT PROFILE:
                - Name: %s
                - Degree: %s in %s
                - College: %s
                - Graduation Year: %s
                - CGPA: %s
                - Experience: %s years
                - Career Goal: %s
                - Dominant Interest Area: %s
                
                ## CURRENT SKILLS:
                %s
                
                ## FEW-SHOT EXAMPLE (Format to follow strictly):
                {
                  "recommendations": [
                    {
                      "rank": 1,
                      "careerTitle": "Full Stack Developer",
                      "confidenceScore": 92.5,
                      "reason": "Strong programming skills with web technologies and practical project experience",
                      "requiredSkills": ["React", "Node.js", "PostgreSQL", "Docker", "REST APIs"],
                      "missingSkills": ["React", "Docker"],
                      "salaryRange": "₹6 LPA - ₹18 LPA",
                      "demandLevel": "Very High",
                      "futureScope": "Excellent growth with AI/ML integration opportunities",
                      "companiesHiring": ["Infosys", "TCS", "Wipro", "Startup ecosystem", "Product companies"],
                      "suggestedProjects": ["E-commerce platform", "Social media app", "RESTful API microservices"]
                    }
                  ]
                }
                
                ## OUTPUT REQUIREMENTS:
                - Return EXACTLY 5 career recommendations ranked by fit score
                - Confidence score between 0-100 (decimal allowed)
                - All salary ranges in Indian Rupees (₹ LPA format)
                - Missing skills must be a subset of required skills not present in current skills
                - Return ONLY valid JSON, no markdown, no explanation text
                - demandLevel: one of [Very High, High, Medium, Low]
                
                Generate the JSON now:
                """.formatted(
                SYSTEM_PROMPT,
                profile.getFullName() != null ? profile.getFullName() : "Student",
                profile.getDegree() != null ? profile.getDegree() : "B.Tech",
                profile.getDepartment() != null ? profile.getDepartment() : "Computer Science",
                profile.getCollege() != null ? profile.getCollege() : "Engineering College",
                profile.getGraduationYear() != null ? profile.getGraduationYear() : "2024",
                profile.getCgpa() != null ? profile.getCgpa() : "Not provided",
                profile.getExperienceYears() != null ? profile.getExperienceYears() : 0,
                profile.getCareerGoal() != null ? profile.getCareerGoal() : "Not specified",
                dominantInterest != null ? dominantInterest : "Technology",
                skillsList.isEmpty() ? "No skills added yet" : skillsList
        );
    }

    // ========================================================
    // SKILL GAP ANALYSIS PROMPT
    // ========================================================
    public String buildSkillGapAnalysisPrompt(List<UserSkill> currentSkills, String targetCareer) {
        String skillsList = currentSkills.stream()
                .map(s -> s.getSkillName() + " (Proficiency: " + s.getProficiency() + "/5)")
                .collect(Collectors.joining(", "));

        return """
                %s
                
                ## TASK: Skill Gap Analysis
                
                Perform a comprehensive skill gap analysis for the target career path.
                
                ## TARGET CAREER: %s
                
                ## CURRENT SKILLS:
                %s
                
                ## OUTPUT FORMAT (Return ONLY valid JSON):
                {
                  "targetCareer": "Software Engineer",
                  "currentStrengths": ["Java", "Spring Boot"],
                  "gapAnalysis": [
                    {
                      "skill": "React",
                      "category": "Frontend",
                      "priorityLevel": "HIGH",
                      "estimatedLearningTime": "6-8 weeks",
                      "resources": ["freeCodeCamp", "React Docs", "Udemy Course"],
                      "reason": "Required for modern web development roles"
                    }
                  ],
                  "overallReadinessScore": 65,
                  "readinessLevel": "Intermediate",
                  "topPriorityAction": "Focus on cloud technologies and DevOps practices"
                }
                
                ## RULES:
                - priorityLevel: CRITICAL, HIGH, MEDIUM, LOW
                - overallReadinessScore: 0-100
                - readinessLevel: Beginner, Intermediate, Advanced, Expert
                - Provide 5-10 skill gaps maximum
                - Return ONLY valid JSON
                
                Generate the JSON now:
                """.formatted(SYSTEM_PROMPT, targetCareer,
                skillsList.isEmpty() ? "No skills provided" : skillsList);
    }

    // ========================================================
    // LEARNING ROADMAP PROMPT
    // ========================================================
    public String buildLearningRoadmapPrompt(String careerTitle, List<String> missingSkills,
                                              int currentWeek, String cgpa) {
        String missingSkillsList = String.join(", ", missingSkills);
        return """
                %s
                
                ## TASK: Generate Personalized 12-Week Learning Roadmap
                
                Create a structured, week-by-week learning roadmap for career transition.
                
                ## CAREER TARGET: %s
                ## SKILLS TO LEARN: %s
                ## STUDENT CGPA: %s
                ## STARTING FROM WEEK: %d
                
                ## OUTPUT FORMAT (Return ONLY valid JSON):
                {
                  "careerTitle": "Data Scientist",
                  "totalWeeks": 12,
                  "monthlyPlan": [
                    {
                      "month": 1,
                      "theme": "Python & Statistics Foundations",
                      "weeks": [
                        {
                          "week": 1,
                          "topic": "Python Basics & NumPy",
                          "tasks": ["Complete Python crash course", "Solve 20 LeetCode easy problems"],
                          "projects": ["Data analysis on COVID dataset"],
                          "platforms": ["HackerRank", "Kaggle"],
                          "interviewMilestone": "Be able to explain list comprehensions"
                        }
                      ]
                    }
                  ],
                  "certifications": [
                    {
                      "name": "Google Data Analytics",
                      "platform": "Coursera",
                      "duration": "6 months",
                      "priority": "HIGH"
                    }
                  ],
                  "practiceResources": ["LeetCode", "HackerRank", "Kaggle"],
                  "estimatedJobReadiness": "Month 4"
                }
                
                ## RULES:
                - Generate exactly 12 weeks organized into 3 months
                - Each week must have specific tasks, not generic advice
                - Include free and paid resources mixed
                - Return ONLY valid JSON
                
                Generate the JSON now:
                """.formatted(SYSTEM_PROMPT, careerTitle,
                missingSkillsList.isEmpty() ? "General skills" : missingSkillsList,
                cgpa != null ? cgpa : "Not provided", currentWeek);
    }

    // ========================================================
    // RESUME ANALYSIS PROMPT
    // ========================================================
    public String buildResumeAnalysisPrompt(String resumeText) {
        return """
                %s
                
                ## TASK: Comprehensive Resume Analysis
                
                Analyze the following resume text and provide detailed feedback.
                
                ## RESUME CONTENT:
                ---
                %s
                ---
                
                ## OUTPUT FORMAT (Return ONLY valid JSON):
                {
                  "extractedInfo": {
                    "name": "John Doe",
                    "skills": ["Java", "Spring Boot", "MySQL"],
                    "projects": ["E-commerce App", "Chat Application"],
                    "achievements": ["Ranked 3rd in hackathon", "Published research paper"],
                    "education": "B.Tech Computer Science",
                    "experience": "2 internships"
                  },
                  "scores": {
                    "atsScore": 72,
                    "formattingScore": 85,
                    "grammarScore": 90,
                    "overallScore": 82
                  },
                  "strengths": ["Good technical skills listed", "Clear project descriptions"],
                  "improvements": [
                    {
                      "section": "Summary",
                      "issue": "Missing professional summary",
                      "suggestion": "Add a 2-3 line professional summary highlighting your key strengths",
                      "priority": "HIGH"
                    }
                  ],
                  "keywordsToAdd": ["Agile", "REST API", "Git", "Docker"],
                  "missingAtsKeywords": ["achievements quantified", "action verbs"],
                  "overallFeedback": "Your resume has a solid foundation. Focus on quantifying achievements and adding a professional summary."
                }
                
                ## RULES:
                - ATS score based on keyword density, formatting, and structure (0-100)
                - Formatting score based on layout, sections, length (0-100)
                - Grammar score based on language quality (0-100)
                - Overall score = weighted average
                - Priority: HIGH, MEDIUM, LOW
                - Return ONLY valid JSON
                
                Generate the JSON now:
                """.formatted(SYSTEM_PROMPT,
                resumeText.length() > 4000 ? resumeText.substring(0, 4000) : resumeText);
    }

    // ========================================================
    // INTERVIEW PREPARATION PROMPT
    // ========================================================
    public String buildInterviewPrepPrompt(String careerTitle, List<UserSkill> skills, String questionType) {
        String skillsList = skills.stream()
                .map(UserSkill::getSkillName)
                .collect(Collectors.joining(", "));

        return """
                %s
                
                ## TASK: Generate Interview Questions and Model Answers
                
                Generate comprehensive interview questions for the target role.
                
                ## CAREER/ROLE: %s
                ## SKILLS: %s
                ## QUESTION TYPE: %s
                
                ## OUTPUT FORMAT (Return ONLY valid JSON):
                {
                  "careerTitle": "Java Backend Developer",
                  "questionType": "TECHNICAL",
                  "questions": [
                    {
                      "question": "Explain the difference between JPA and Hibernate.",
                      "difficulty": "MEDIUM",
                      "modelAnswer": "JPA is a specification/API for ORM in Java, while Hibernate is an implementation of JPA. JPA defines standard annotations like @Entity, @Id etc., and Hibernate provides the actual implementation along with additional features like caching and custom query language (HQL).",
                      "tips": "Mention specification vs implementation difference, give examples of other JPA implementations"
                    }
                  ]
                }
                
                ## RULES:
                - Generate exactly 10 questions
                - difficulty: EASY, MEDIUM, HARD
                - questionType: TECHNICAL, HR, CODING, BEHAVIORAL, COMPANY_SPECIFIC
                - Model answers should be comprehensive but concise (3-5 sentences)
                - Return ONLY valid JSON
                
                Generate the JSON now:
                """.formatted(SYSTEM_PROMPT, careerTitle,
                skillsList.isEmpty() ? "General" : skillsList, questionType);
    }

    // ========================================================
    // COURSE RECOMMENDATION PROMPT
    // ========================================================
    public String buildCourseRecommendationPrompt(List<String> missingSkills, String careerTitle) {
        return """
                %s
                
                ## TASK: Personalized Course Recommendations
                
                Recommend the best courses for the missing skills to achieve the career goal.
                
                ## CAREER GOAL: %s
                ## SKILLS TO LEARN: %s
                
                ## OUTPUT FORMAT (Return ONLY valid JSON):
                {
                  "recommendations": [
                    {
                      "skill": "React",
                      "courses": [
                        {
                          "title": "React - The Complete Guide",
                          "provider": "Udemy",
                          "url": "https://www.udemy.com/course/react-the-complete-guide-incl-redux/",
                          "type": "PAID",
                          "duration": "40 hours",
                          "rating": 4.8,
                          "description": "Comprehensive React course with Hooks, Redux, and more"
                        },
                        {
                          "title": "React Documentation",
                          "provider": "React Official",
                          "url": "https://react.dev",
                          "type": "FREE",
                          "duration": "Self-paced",
                          "rating": 5.0,
                          "description": "Official React documentation with interactive examples"
                        }
                      ]
                    }
                  ]
                }
                
                ## RULES:
                - Provide 2-3 course options per skill (mix of FREE, PAID, YOUTUBE)
                - Use real, verifiable course URLs from Coursera, Udemy, YouTube, freeCodeCamp
                - Return ONLY valid JSON
                
                Generate the JSON now:
                """.formatted(SYSTEM_PROMPT, careerTitle, String.join(", ", missingSkills));
    }

    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}
