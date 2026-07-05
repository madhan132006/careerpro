package com.careerpro.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CareerProApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareerProApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════════════════╗
                ║   AI Career Guidance Agent - Backend Running     ║
                ║   Swagger UI: http://localhost:8080/api/swagger-ui.html ║
                ╚══════════════════════════════════════════════════╝
                """);
    }
}
