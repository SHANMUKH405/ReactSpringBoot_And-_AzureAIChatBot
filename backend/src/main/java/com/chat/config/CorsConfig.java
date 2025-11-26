package com.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration
 * 
 * CORS = Cross-Origin Resource Sharing
 * 
 * Why we need this:
 * - Frontend runs on http://localhost:3000
 * - Backend runs on http://localhost:8080
 * - Browsers block requests between different origins (security)
 * - This config allows frontend to call backend APIs
 * 
 * @Configuration tells Spring this is a configuration class
 * Spring reads this when the application starts
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${spring.web.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;
    
    /**
     * Configures CORS settings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply to all /api endpoints
                .allowedOrigins(allowedOrigins.split(",")) // Allow these origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow cookies/credentials
                .maxAge(3600); // Cache preflight response for 1 hour
    }
    
    // Removed duplicate CORS configuration to avoid conflicts
    // Using only addCorsMappings method above
}

