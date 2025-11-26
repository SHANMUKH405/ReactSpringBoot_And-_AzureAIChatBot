package com.chat.service;

import com.chat.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * AI Service - Handles communication with AI APIs
 * 
 * This service is responsible for:
 * 1. Building requests to AI API (OpenAI, Hugging Face, etc.)
 * 2. Sending HTTP requests to AI API
 * 3. Parsing responses from AI API
 * 4. Error handling
 * 
 * @Service annotation tells Spring this is a service component
 * Spring will automatically create an instance and inject it where needed
 */
@Slf4j
@Service
public class AIService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    @Value("${ai.api.url}")
    private String apiUrl;
    
    @Value("${ai.model.name}")
    private String modelName;
    
    @Value("${ai.api.timeout:30000}")
    private int timeout;
    
    @Value("${app.url:https://react-spring-boot-and-azure-ai-chat.vercel.app}")
    private String appUrl;
    
    /**
     * Constructor - Creates WebClient for making HTTP requests
     * 
     * WebClient is Spring's reactive HTTP client
     * It's better than RestTemplate for modern Spring Boot apps
     */
    public AIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Sends a message to AI API and gets response
     * 
     * @param userMessage The user's message
     * @param conversationHistory Previous messages in the conversation (for context)
     * @return AI's response as a String
     */
    public String getAIResponse(String userMessage, List<Message> conversationHistory) {
        try {
            log.info("Sending message to AI: {}", userMessage);
            
            // Build the request body for AI API
            Map<String, Object> requestBody = buildRequestBody(userMessage, conversationHistory);
            
            // Make HTTP POST request to AI API
            // OpenRouter requires additional headers
            String response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header("HTTP-Referer", appUrl) // OpenRouter: Your app URL
                    .header("X-Title", "AI Chat Assistant") // OpenRouter: Your app name
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("AI API error: HTTP {} - Response: {}", clientResponse.statusCode(), errorBody);
                                    
                                    // Check for authentication errors
                                    if (clientResponse.statusCode().value() == 401) {
                                        return Mono.error(new RuntimeException("API_KEY_INVALID: " + errorBody));
                                    }
                                    return Mono.error(new RuntimeException("AI_API_ERROR_" + clientResponse.statusCode() + ": " + errorBody));
                                });
                    })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block(); // Blocking call - waits for response
            
            // Parse the response to extract AI's message
            return parseAIResponse(response);
            
        } catch (Exception e) {
            log.error("Error calling AI API: {}", e.getMessage(), e);
            
            // Check if it's an authentication error
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("API_KEY_INVALID")) {
                return "⚠️ **API Key Error**: Your OpenRouter API key is invalid or expired.\n\n" +
                       "Please:\n" +
                       "1. Go to https://openrouter.ai/keys to get a new API key\n" +
                       "2. Update it in `backend/src/main/resources/application.properties`\n" +
                       "3. Restart the backend server\n\n" +
                       "Or set it as an environment variable: `export AI_API_KEY=your-new-key`";
            }
            
            // Fallback response if AI API fails
            return "I apologize, but I'm having trouble connecting to the AI service. " +
                   "Please check your API key and try again later.\n\n" +
                   "Error details: " + (errorMessage != null ? errorMessage.substring(0, Math.min(200, errorMessage.length())) : "Unknown error");
        }
    }
    
    /**
     * Builds the request body for AI API
     * 
     * Different AI APIs have different formats, but most follow this structure:
     * {
     *   "model": "gpt-3.5-turbo",
     *   "messages": [
     *     {"role": "system", "content": "You are a helpful assistant."},
     *     {"role": "user", "content": "Hello"}
     *   ]
     * }
     */
    private Map<String, Object> buildRequestBody(String userMessage, List<Message> history) {
        Map<String, Object> body = new HashMap<>();
        
        // Add model name
        body.put("model", modelName);
        
        // Build messages array
        List<Map<String, String>> messages = new ArrayList<>();
        
        // Add system message (sets AI's behavior)
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful, friendly, and knowledgeable AI assistant. " +
                "Answer questions clearly and concisely.");
        messages.add(systemMessage);
        
        // Add conversation history (if any)
        if (history != null && !history.isEmpty()) {
            for (Message msg : history) {
                Map<String, String> msgMap = new HashMap<>();
                msgMap.put("role", msg.getRole());
                msgMap.put("content", msg.getContent());
                messages.add(msgMap);
            }
        }
        
        // Add current user message
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        body.put("messages", messages);
        
        // Additional parameters
        body.put("temperature", 0.7); // Controls randomness (0.0 = deterministic, 1.0 = creative)
        body.put("max_tokens", 500);  // Maximum length of response
        
        return body;
    }
    
    /**
     * Parses the JSON response from AI API to extract the message
     * 
     * AI API response structure:
     * {
     *   "choices": [
     *     {
     *       "message": {
     *         "content": "AI's response here"
     *       }
     *     }
     *   ]
     * }
     */
    private String parseAIResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Navigate JSON structure to get the content
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.path("message");
                String content = message.path("content").asText();
                
                if (content != null && !content.isEmpty()) {
                    return content.trim();
                }
            }
            
            // If parsing fails, return error message
            log.warn("Unexpected response format: {}", jsonResponse);
            return "Sorry, I received an unexpected response format.";
            
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            return "Sorry, I had trouble processing the response.";
        }
    }
    
    /**
     * Health check - tests if AI API is accessible
     */
    public boolean isHealthy() {
        try {
            // Simple check - you could make a lightweight request here
            return apiKey != null && !apiKey.equals("your-api-key-here") && !apiKey.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}

