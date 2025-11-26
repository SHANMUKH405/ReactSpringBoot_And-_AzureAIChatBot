package com.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * Configures Spring Security for authentication
 * 
 * For now, we're keeping it simple - allowing all API endpoints
 * In production, you'd add proper authentication (JWT tokens, etc.)
 * 
 * @Configuration - Marks this as configuration class
 * @EnableWebSecurity - Enables Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Password Encoder Bean
     * 
     * BCrypt is a strong password hashing algorithm
     * Spring Boot automatically uses this to encrypt passwords
     * 
     * @Bean - Spring creates this object automatically
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Security Filter Chain
     * 
     * Configures which endpoints are public and which require authentication
     * 
     * For learning purposes, we're allowing all endpoints
     * In production, you'd protect endpoints with authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API (we'll enable it properly in production)
            .csrf(csrf -> csrf.disable())
            
            // Allow all endpoints (for learning - no authentication required)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // H2 database console
                .anyRequest().permitAll()
            )
            
            // Allow H2 console frames (using modern API)
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            
            // Stateless session (no cookies for now)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}

