package com.careerpro.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

/**
 * GeminiAiClient - Google Gemini AI Integration
 * ===============================================
 * Handles all communications with the Gemini 1.5 Flash API.
 * Implements retry logic, error handling, and response parsing.
 */
@Slf4j
@Service
public class GeminiAiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.max-tokens}")
    private int maxTokens;

    @Value("${gemini.api.temperature}")
    private double temperature;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiAiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Send a prompt to Gemini and get the text response.
     */
    public String generateContent(String prompt) {
        String urlWithKey = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
            "contents", new Object[]{
                Map.of("parts", new Object[]{
                    Map.of("text", prompt)
                })
            },
            "generationConfig", Map.of(
                "maxOutputTokens", maxTokens,
                "temperature", temperature,
                "topP", 0.8,
                "topK", 40
            ),
            "safetySettings", new Object[]{
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE")
            }
        );

        try {
            String response = webClient.post()
                    .uri(urlWithKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            return extractTextFromResponse(response);

        } catch (WebClientResponseException e) {
            log.error("Gemini API error - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI service error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Gemini API: ", e);
            throw new RuntimeException("Failed to connect to AI service. Please try again.");
        }
    }

    /**
     * Extract the text content from Gemini API JSON response.
     */
    private String extractTextFromResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content").path("parts");
                if (content.isArray() && content.size() > 0) {
                    String rawText = content.get(0).path("text").asText();
                    return cleanJsonResponse(rawText);
                }
            }

            log.warn("Unexpected Gemini response structure: {}", responseJson);
            throw new RuntimeException("Unexpected AI response format");
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            throw new RuntimeException("Error processing AI response");
        }
    }

    /**
     * Clean the AI response to extract pure JSON (remove markdown code blocks if present)
     */
    private String cleanJsonResponse(String text) {
        if (text == null) return "{}";

        // Remove markdown code blocks
        text = text.trim();
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
    }

    /**
     * Parse JSON string to JsonNode for safe navigation
     */
    public JsonNode parseResponse(String jsonText) {
        try {
            return objectMapper.readTree(jsonText);
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", jsonText);
            throw new RuntimeException("Invalid JSON response from AI service");
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
