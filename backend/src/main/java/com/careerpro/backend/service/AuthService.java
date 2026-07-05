package com.careerpro.backend.service;

import com.careerpro.backend.dto.AuthResponse;
import com.careerpro.backend.dto.LoginRequest;
import com.careerpro.backend.dto.RegisterRequest;
import com.careerpro.backend.email.EmailService;
import com.careerpro.backend.entity.Role;
import com.careerpro.backend.entity.User;
import com.careerpro.backend.entity.UserProfile;
import com.careerpro.backend.exception.BadRequestException;
import com.careerpro.backend.exception.ResourceNotFoundException;
import com.careerpro.backend.repository.UserProfileRepository;
import com.careerpro.backend.repository.UserRepository;
import com.careerpro.backend.security.JwtTokenProvider;
import com.careerpro.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered. Please login or use a different email.");
        }

        // Get default USER role (id=1)
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        // Create user
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .isActive(true)
                .isEmailVerified(false)
                .role(userRole)
                .build();

        user = userRepository.save(user);

        // Create empty profile
        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(profile);

        // Generate token
        String token = tokenProvider.generateTokenFromUserId(user.getId(), user.getEmail());

        // Send welcome email async
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        log.info("New user registered: {}", user.getEmail());

        return AuthResponse.success(token, user.getId(), user.getEmail(),
                user.getFullName(), user.getRole().getName());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.success(token, user.getId(), user.getEmail(),
                user.getFullName(), user.getRole().getName());
    }

    @Transactional
    public String generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp, user.getFullName());
        return "OTP sent to " + maskEmail(email);
    }

    @Transactional
    public AuthResponse verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setIsEmailVerified(true);
        userRepository.save(user);

        String token = tokenProvider.generateTokenFromUserId(user.getId(), user.getEmail());
        return AuthResponse.success(token, user.getId(), user.getEmail(),
                user.getFullName(), user.getRole().getName());
    }

    @Transactional
    public String verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        user.setIsEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return "Email verified successfully!";
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
