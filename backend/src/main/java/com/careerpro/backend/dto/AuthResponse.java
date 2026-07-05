package com.careerpro.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String message;

    public static AuthResponse success(String token, Long userId, String email, String fullName, String role) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userId)
                .email(email)
                .fullName(fullName)
                .role(role)
                .message("Authentication successful")
                .build();
    }
}
