package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.entity.Feedback;
import com.careerpro.backend.entity.Notification;
import com.careerpro.backend.entity.User;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.FeedbackRepository;
import com.careerpro.backend.repository.NotificationRepository;
import com.careerpro.backend.repository.UserRepository;
import com.careerpro.backend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Notifications & Feedback", description = "User notifications and feedback APIs")
public class NotificationFeedbackController {

    private final NotificationRepository notificationRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    // ==== NOTIFICATIONS ====

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.getId())));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    @PatchMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndIsReadFalse(principal.getId());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    // ==== FEEDBACK / CONTACT ====

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        User user = null;
        if (principal != null) {
            user = userRepository.findById(principal.getId()).orElse(null);
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .name(body.getOrDefault("name", "").toString())
                .email(body.getOrDefault("email", "").toString())
                .subject(body.getOrDefault("subject", "").toString())
                .message(body.getOrDefault("message", "").toString())
                .rating(Integer.parseInt(body.getOrDefault("rating", "5").toString()))
                .build();

        feedback = feedbackRepository.save(feedback);
        return ResponseEntity.ok(ApiResponse.success(feedback, "Thank you for your feedback!"));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<?>>> getCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            com.careerpro.backend.repository.CourseRepository courseRepository) {

        List<?> courses;
        if (type != null) {
            courses = courseRepository.findByTypeAndIsActiveTrue(type);
        } else if (category != null) {
            courses = courseRepository.findByCategoryAndIsActiveTrue(category);
        } else {
            courses = courseRepository.findByIsActiveTrueOrderByRatingDesc();
        }
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
}
