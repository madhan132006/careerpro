package com.careerpro.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Career Guidance Agent API")
                        .description("""
                                Intelligent Career Recommendation System Using Java, Spring Boot, MySQL and Google Gemini AI.
                                
                                This API provides:
                                - User Authentication (Register, Login, OTP, JWT)
                                - Profile Management
                                - Skill Assessment
                                - Career Interest Assessment
                                - AI Career Recommendations (Gemini)
                                - Skill Gap Analysis
                                - Learning Roadmap Generation
                                - Resume Analysis (PDF)
                                - Interview Preparation
                                - Admin Panel
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CareerPro AI Team")
                                .email("support@careerpro.ai")
                                .url("https://careerpro.ai"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /auth/login")));
    }
}
