package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.entity.*;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Panel", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final CareerRecommendationRepository careerRecommendationRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.countRegularUsers());
        stats.put("totalRecommendations", careerRecommendationRepository.count());
        stats.put("totalResumesAnalyzed", resumeAnalysisRepository.count());
        stats.put("totalFeedbacks", feedbackRepository.count());
        stats.put("openFeedbacks", feedbackRepository.findByStatusOrderByCreatedAtDesc("OPEN").size());
        stats.put("totalCourses", courseRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all registered users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", u.getId());
                    map.put("fullName", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("phone", u.getPhone());
                    map.put("role", u.getRole().getName());
                    map.put("isActive", u.getIsActive());
                    map.put("isEmailVerified", u.getIsEmailVerified());
                    map.put("createdAt", u.getCreatedAt());
                    return map;
                }).toList();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Toggle user active status")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        String status = user.getIsActive() ? "activated" : "deactivated";
        return ResponseEntity.ok(ApiResponse.success("User " + status + " successfully"));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @GetMapping("/feedback")
    @Operation(summary = "Get all feedback")
    public ResponseEntity<ApiResponse<List<Feedback>>> getAllFeedback() {
        return ResponseEntity.ok(ApiResponse.success(feedbackRepository.findAllByOrderByCreatedAtDesc()));
    }

    @PatchMapping("/feedback/{id}/status")
    @Operation(summary = "Update feedback status")
    public ResponseEntity<ApiResponse<Feedback>> updateFeedbackStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
        feedback.setStatus(body.get("status"));
        feedback = feedbackRepository.save(feedback);
        return ResponseEntity.ok(ApiResponse.success(feedback, "Feedback status updated"));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseRepository.findAll()));
    }

    @PostMapping("/courses")
    @Operation(summary = "Add a new course")
    public ResponseEntity<ApiResponse<Course>> addCourse(@RequestBody Course course) {
        Course saved = courseRepository.save(course);
        return ResponseEntity.ok(ApiResponse.success(saved, "Course added successfully"));
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Update a course")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(ApiResponse.success(courseRepository.save(course), "Course updated"));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Course deleted successfully"));
    }
}
