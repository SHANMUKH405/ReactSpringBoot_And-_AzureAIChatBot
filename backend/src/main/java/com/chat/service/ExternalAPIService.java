package com.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * External API Service - Example of integrating with external APIs
 * 
 * This service demonstrates how to call external APIs:
 * - Weather APIs
 * - News APIs
 * - Any REST API
 * 
 * This is a LEARNING EXAMPLE to show API integration patterns
 */
@Slf4j
@Service
public class ExternalAPIService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String weatherApiUrl;
    
    @Value("${weather.api.key:}")
    private String weatherApiKey;
    
    /**
     * Constructor
     */
    public ExternalAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Example: Get weather for a city
     * 
     * This shows how to:
     * 1. Build a request with query parameters
     * 2. Call an external API
     * 3. Parse the JSON response
     * 4. Handle errors
     * 
     * @param city City name
     * @return Weather information as a Map
     */
    public Map<String, Object> getWeather(String city) {
        try {
            log.info("Fetching weather for city: {}", city);
            
            // Make GET request to weather API
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(weatherApiUrl)
                            .queryParam("q", city)
                            .queryParam("appid", weatherApiKey)
                            .queryParam("units", "metric") // Celsius
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Parse JSON response
            JsonNode json = objectMapper.readTree(response);
            
            // Extract relevant information
            Map<String, Object> weatherInfo = new HashMap<>();
            weatherInfo.put("city", json.path("name").asText());
            weatherInfo.put("temperature", json.path("main").path("temp").asDouble() + "°C");
            weatherInfo.put("description", json.path("weather").get(0).path("description").asText());
            weatherInfo.put("humidity", json.path("main").path("humidity").asInt() + "%");
            
            return weatherInfo;
            
        } catch (Exception e) {
            log.error("Error fetching weather: {}", e.getMessage());
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "Could not fetch weather. Please check your API key.");
            errorInfo.put("message", e.getMessage());
            return errorInfo;
        }
    }
    
    /**
     * Example: Get news headlines
     * 
     * This is a placeholder showing the pattern for another API
     * You would implement this similarly to getWeather()
     */
    public Map<String, Object> getNews(String topic) {
        // Example implementation pattern:
        // 1. Build request URL with parameters
        // 2. Add API key in headers or query params
        // 3. Make HTTP GET request
        // 4. Parse JSON response
        // 5. Return formatted data
        
        Map<String, Object> newsInfo = new HashMap<>();
        newsInfo.put("message", "News API integration - implement similar to getWeather()");
        newsInfo.put("topic", topic);
        return newsInfo;
    }
}

