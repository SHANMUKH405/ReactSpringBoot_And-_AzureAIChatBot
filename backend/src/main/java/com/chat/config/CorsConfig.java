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
 * - Frontend runs on http://localhost:3000 or Vercel
 * - Backend runs on Azure
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
     * CORS Configuration Source for Spring Security
     * This is needed to integrate CORS with Spring Security filter chain
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    /**
     * Configures CORS settings for MVC
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
}

