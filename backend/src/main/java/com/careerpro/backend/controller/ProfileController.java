package com.careerpro.backend.controller;

import com.careerpro.backend.dto.ApiResponse;
import com.careerpro.backend.dto.UserProfileDto;
import com.careerpro.backend.security.UserPrincipal;
import com.careerpro.backend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final UserProfileService profileService;

    @GetMapping
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserProfileDto profile = profileService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserProfileDto dto) {
        UserProfileDto updated = profileService.updateProfile(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated successfully!"));
    }

    @PostMapping("/photo")
    @Operation(summary = "Upload profile photo")
    public ResponseEntity<ApiResponse<String>> uploadPhoto(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = profileService.uploadProfilePhoto(principal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success(url, "Profile photo uploaded successfully!"));
    }
}
