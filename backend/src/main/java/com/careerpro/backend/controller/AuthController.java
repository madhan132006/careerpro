package com.careerpro.backend.controller;

import com.careerpro.backend.dto.*;
import com.careerpro.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, OTP, Password Reset APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful! Welcome to CareerPro AI."));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful!"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send OTP for password reset")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody Map<String, String> body) {
        String message = authService.generateAndSendOtp(body.get("email"));
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP for email verification")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @RequestBody Map<String, String> body) {
        String result = authService.verifyOtp(body.get("email"), body.get("otp"));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with OTP verification")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(
            @RequestBody Map<String, String> body) {
        AuthResponse response = authService.verifyOtpAndResetPassword(
                body.get("email"), body.get("otp"), body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success(response, "Password reset successful!"));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP to email")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @RequestBody Map<String, String> body) {
        String message = authService.generateAndSendOtp(body.get("email"));
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
