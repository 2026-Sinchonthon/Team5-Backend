package com.sinchonthon.team5.odyssey.jobpost.llm;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String defaultModel;

    public OllamaClient(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String defaultModel,
            @Value("${ollama.timeout-seconds}") int timeoutSeconds,
            ObjectMapper objectMapper
    ) {
        this.defaultModel = defaultModel;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(clientHttpRequestFactory(timeoutSeconds))
                .build();
    }

    private org.springframework.http.client.ClientHttpRequestFactory clientHttpRequestFactory(int timeoutSeconds) {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(timeoutSeconds));
        factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));
        return factory;
    }

    public String chatJson(List<Map<String, String>> messages) {
        return chatJson(messages, defaultModel);
    }

    public String chatJson(List<Map<String, String>> messages, String model) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", messages,
                "stream", false,
                "format", "json",
                "options", Map.of("temperature", 0.1)
        );

        try {
            String rawResponse = restClient.post()
                    .uri("/api/chat")
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            String content = root.path("message").path("content").asText("");
            if (content.isBlank()) {
                throw new OllamaException("Ollama 응답에 content가 비어 있음");
            }
            return content;
        } catch (OllamaException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new OllamaException("Ollama 통신 실패: " + exception.getMessage(), exception);
        }
    }

    public boolean isAlive() {
        try {
            restClient.get().uri("/api/tags").retrieve().toBodilessEntity();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
