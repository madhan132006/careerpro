package com.careerpro.backend.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otp, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "CareerPro AI");
            helper.setTo(toEmail);
            helper.setSubject("🔑 CareerPro AI - Your OTP Verification Code");
            helper.setText(buildOtpEmailHtml(fullName, otp), true);

            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "CareerPro AI");
            helper.setTo(toEmail);
            helper.setSubject("🎉 Welcome to CareerPro AI - Your Career Journey Begins!");
            helper.setText(buildWelcomeEmailHtml(fullName), true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildOtpEmailHtml(String name, String otp) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f0f4ff; padding: 20px;">
                  <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 12px;
                              padding: 40px; box-shadow: 0 4px 20px rgba(37,99,235,0.1);">
                    <div style="text-align: center; margin-bottom: 30px;">
                      <h1 style="color: #2563eb; font-size: 28px;">🎯 CareerPro AI</h1>
                      <p style="color: #64748b; font-size: 14px;">Intelligent Career Guidance System</p>
                    </div>
                    <h2 style="color: #1e293b;">Hello, %s!</h2>
                    <p style="color: #475569;">Your OTP verification code is:</p>
                    <div style="text-align: center; margin: 30px 0;">
                      <span style="font-size: 48px; font-weight: bold; letter-spacing: 12px;
                                   color: #2563eb; background: #eff6ff; padding: 20px 40px;
                                   border-radius: 12px; border: 2px dashed #93c5fd;">%s</span>
                    </div>
                    <p style="color: #64748b;">This OTP is valid for <strong>10 minutes</strong>. Do not share it with anyone.</p>
                    <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 30px 0;">
                    <p style="color: #94a3b8; font-size: 12px; text-align: center;">
                      © 2024 CareerPro AI. All rights reserved.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(name, otp);
    }

    private String buildWelcomeEmailHtml(String name) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f0f4ff; padding: 20px;">
                  <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 12px;
                              padding: 40px; box-shadow: 0 4px 20px rgba(37,99,235,0.1);">
                    <div style="text-align: center; margin-bottom: 30px;">
                      <h1 style="color: #2563eb; font-size: 28px;">🎯 CareerPro AI</h1>
                    </div>
                    <h2 style="color: #1e293b;">Welcome, %s! 🎉</h2>
                    <p style="color: #475569;">Your account has been successfully created. Your intelligent career journey begins now!</p>
                    <div style="background: linear-gradient(135deg, #2563eb, #7c3aed); padding: 20px;
                                border-radius: 8px; margin: 20px 0;">
                      <p style="color: white; margin: 0;">
                        ✅ Complete your profile<br>
                        ✅ Add your skills<br>
                        ✅ Take the career assessment<br>
                        ✅ Get AI-powered recommendations
                      </p>
                    </div>
                    <p style="color: #64748b;">Start by completing your profile to get personalized career recommendations!</p>
                    <p style="color: #94a3b8; font-size: 12px; text-align: center; margin-top: 30px;">
                      © 2024 CareerPro AI. All rights reserved.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(name);
    }
}
